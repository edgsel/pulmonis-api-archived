package com.pulmonis.pulmonisapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.pulmonis.pulmonisapi.controller.dto.request.ForgottenPasswordChangeRequest
import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.JwtTokenValidationException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.exception.UnauthorizedException
import com.pulmonis.pulmonisapi.controller.dto.request.JwtRequest
import com.pulmonis.pulmonisapi.controller.dto.request.PasswordChangeRequest
import com.pulmonis.pulmonisapi.controller.dto.request.PasswordResetRequest
import com.pulmonis.pulmonisapi.controller.dto.request.UserRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.JwtResponse
import com.pulmonis.pulmonisapi.controller.dto.response.UserResponse
import com.pulmonis.pulmonisapi.enums.MailType
import com.pulmonis.pulmonisapi.exception.ValidationException
import com.pulmonis.pulmonisapi.factory.JwtBlacklistFactory
import com.pulmonis.pulmonisapi.factory.UserFactory
import com.pulmonis.pulmonisapi.enums.UserStatus
import com.pulmonis.pulmonisapi.exception.InvalidDataException
import com.pulmonis.pulmonisapi.factory.MailFactory
import com.pulmonis.pulmonisapi.hibernate.entities.User
import com.pulmonis.pulmonisapi.hibernate.repository.JwtBlacklistRepository
import com.pulmonis.pulmonisapi.hibernate.repository.PasswordTokenRepository
import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import com.pulmonis.pulmonisapi.mail.MailSenderService
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.security.JwtUserDetailsService
import com.pulmonis.pulmonisapi.security.bCryptPasswordEncoder
import com.pulmonis.pulmonisapi.service.PasswordResetService
import com.pulmonis.pulmonisapi.util.JwtTokenUtil
import com.pulmonis.pulmonisapi.util.URIUtil
import com.pulmonis.pulmonisapi.validator.PasswordChangeValidator
import com.pulmonis.pulmonisapi.validator.UserLoginValidator
import com.pulmonis.pulmonisapi.validator.UserRequestValidator
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import javax.servlet.http.HttpServletRequest
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.time.LocalDateTime

@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val userRepository: UserRepository,
    private val jwtBlacklistRepository: JwtBlacklistRepository,
    private val passwordTokenRepository: PasswordTokenRepository,
    private val objectMapper: ObjectMapper,
    private val mailSender: MailSenderService
) {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationController::class.java)
    private var httpStatusOK = HttpStatus.OK.value()
    private val passwordResetService = PasswordResetService(this.passwordTokenRepository)

    @RequestMapping("user/signup", method = [RequestMethod.POST])
    @Throws(BadRequestException::class)
    fun registerUser(
        @RequestBody request: UserRequestBody
    ): ResponseEntity<UserResponse> {
        try {
            UserRequestValidator.validate(request)
        } catch (e: ValidationException) {
            throw BadRequestException("User request body validation failed: ${e.message}", e)
        }

        if (userRepository.findByEmail(request.email) != null) {
            throw BadRequestException("User already exists")
        }

        val newUser = UserFactory.build(request)
        val savedUser = userDetailsService.save(newUser)
        logger.info("New user created")

        val mail = try {
            MailFactory.build(savedUser, MailType.NEW_USER)
        } catch (e: InvalidDataException) {
            logger.error("Failed to build mail: ${e.message}", e)
            throw BadRequestException("Failed to build mail: ${e.message}", e)
        }

        val emailTemplate = try {
            MailFactory.getEmailTemplateOrThrow(MailType.NEW_USER)
        } catch (e: InvalidDataException) {
            logger.error("Failed to get mail template: ${e.message}", e)
            throw BadRequestException("Failed to get mail template: ${e.message}", e)
        }

        val isMailSent = mailSender.sendTemplateEmail(mail, emailTemplate)

        if (!isMailSent) {
            logger.error("Email was not sent to a new user")
            throw BadRequestException("Email was not sent to a new user")
        }

        Sentry.addBreadcrumb("Status: $httpStatusOK")

        return ResponseEntity(
            UserFactory.buildUserResponse(savedUser),
            httpStatusOK
        )
    }

    @RequestMapping("user/login", method = [RequestMethod.POST])
    @Throws(BadRequestException::class)
    fun authenticateUser(
        @RequestBody authRequest: JwtRequest
    ): ResponseEntity<JwtResponse> {
        try {
            UserLoginValidator.validate(authRequest)
        } catch (e: ValidationException) {
            throw PreconditionFailedException("User validation failed: ${e.message}", e)
        }

        val user = userRepository.findByEmail(authRequest.email)
            ?: throw BadRequestException("Invalid Credentials")

        if (user.status != UserStatus.active) {
            throw BadRequestException("Your account is not active")
        }

        val isPasswordValid = UserLoginValidator.validateUserPassword(authRequest.password!!, user.password!!)

        if (!isPasswordValid) {
            throw BadRequestException("Invalid Credentials")
        }

        val userDetails = try {
            userDetailsService.loadUserByUsername(user.email)
        } catch (e: UsernameNotFoundException) {
            throw NotFoundException("User not found")
        }

        val token = jwtTokenUtil.generateToken(userDetails, user)
        val response = JwtResponse().also {
            it.token = token
        }

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("User logged in")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("user/logout", method = [RequestMethod.DELETE])
    @Throws(BadRequestException::class)
    fun logoutUser(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Map<String, Boolean>> {
        val formattedToken = try {
            jwtTokenUtil.formatToken(token)
        } catch (e: MalformedJwtException) {
            throw JwtTokenValidationException("Failed to format JWT token", e)
        }
        val blacklistedToken = JwtBlacklistFactory.build(formattedToken)
        jwtBlacklistRepository.save(blacklistedToken)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("User logged out")

        val response = mapOf("ok" to true)
        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/refresh-token", method = [RequestMethod.GET])
    fun refreshToken(
        @RequestHeader("Authorization") requestToken: String
    ): ResponseEntity<JwtResponse> {
        try {
            if (jwtBlacklistRepository.findFirstByToken(requestToken) != null) {
                throw UnauthorizedException("Token is not valid")
            }
        } catch (e: MalformedJwtException) {
            throw UnauthorizedException("Token is not correctly constructed", e)
        } catch (e: SignatureException) {
            throw UnauthorizedException("Token signature is not valid", e)
        }

        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(requestToken)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val userDetails = try {
            userDetailsService.loadUserByUsername(user.email)
        } catch (e: UsernameNotFoundException) {
            throw NotFoundException("User not found")
        }

        val oldFormattedToken = try {
            jwtTokenUtil.formatToken(requestToken)
        } catch (e: MalformedJwtException) {
            throw JwtTokenValidationException("Failed to format JWT token", e)
        }

        val blacklistedToken = JwtBlacklistFactory.build(oldFormattedToken)
        jwtBlacklistRepository.save(blacklistedToken)

        val updatedToken = jwtTokenUtil.generateToken(userDetails, user)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("User token refreshed")

        val response = JwtResponse().also {
            it.token = updatedToken
        }

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("user/update", method = [RequestMethod.PATCH])
    @Throws(BadRequestException::class)
    fun updateUser(
        @RequestHeader("Authorization") token: String,
        request: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val emailBefore = user.email
        val passwordBefore = user.password

        val incomingUpdates = try {
            objectMapper.readerForUpdating(user).readValue(request.reader, User::class.java)
        } catch (e: IOException) {
            throw PreconditionFailedException("Failed to process request body: ${e.message}")
        }

        if (incomingUpdates.email != emailBefore || incomingUpdates.password != passwordBefore) {
            throw BadRequestException("Can not change password or email")
        }

        val updatedUser = userRepository.saveAndFlush(incomingUpdates)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) User updated")

        return ResponseEntity(
            UserResponse().also {
                it.email = updatedUser.email
            },
            httpStatusOK
        )
    }

    @RequestMapping("user/change-password", method = [RequestMethod.PUT])
    fun changeUserPassword(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: PasswordChangeRequest
    ): ResponseEntity<Map<String, Boolean>> {
        try {
            PasswordChangeValidator.validate(request)
        } catch (e: ValidationException) {
            throw PreconditionFailedException("Validation failed: ${e.message}", e)
        }
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Failed to get user id from token")
        var user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User not found")

        val isOldPasswordValid = UserLoginValidator.validateUserPassword(request.oldPassword!!, user.password!!)

        if (!isOldPasswordValid) {
            throw BadRequestException("Old password is invalid")
        }

        user = user.also {
            it.password = bCryptPasswordEncoder().encode(request.newPassword)
        }
        userRepository.save(user)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) User password changed")

        return ResponseEntity(
            mapOf("ok" to true),
            httpStatusOK
        )
    }

    @RequestMapping("user/reset-password", method = [RequestMethod.POST])
    fun resetUserPassword(
        @RequestBody request: PasswordResetRequest
    ): ResponseEntity<Map<String, Boolean>> {
        if (request.email.isNullOrEmpty()) {
            throw BadRequestException("Email is empty")
        }
        logger.info("User with email ${request.email} requested a password recovery")

        val user = userRepository.findByEmail(request.email)

        val passwordToken = if (user != null) {
            passwordResetService.buildPasswordResetToken(user)
        } else {
            null
        }

        if (!passwordToken.isNullOrEmpty()) {
            val passwordResetLink = URIUtil.buildPasswordResetUri(passwordToken)

            val mail = try {
                // by this moment user should be found
                MailFactory.build(user!!, MailType.PASSWORD_RECOVERY, passwordResetLink)
            } catch (e: InvalidDataException) {
                logger.error(e.message, e)
                throw BadRequestException("Failed to generate mail: ${e.message}", e)
            }

            val emailTemplate = try {
                MailFactory.getEmailTemplateOrThrow(MailType.PASSWORD_RECOVERY)
            } catch (e: InvalidDataException) {
                logger.error("Failed to get mail template: ${e.message}", e)
                throw BadRequestException("Failed to get mail template: ${e.message}", e)
            }

            val isMailSent = mailSender.sendTemplateEmail(mail, emailTemplate)

            if (!isMailSent) {
                logger.warn("Failed to send email...ignoring")
            }
            Sentry.addBreadcrumb("Status: $httpStatusOK")
        } else {
            logger.warn("Email ${request.email} is not found. Silently ignoring...")
        }

        return ResponseEntity(
            mapOf("ok" to true),
            httpStatusOK
        )
    }

    @RequestMapping("user/validate-password-token", method = [RequestMethod.GET])
    fun validatePasswordResetToken(
        @RequestParam("token") passwordRecoveryToken: String?
    ): ResponseEntity<Map<String, Boolean>> {
        logger.info("Password token recovery request")
        return ResponseEntity(
            mapOf(
                "isValid" to PasswordResetService(passwordTokenRepository).validatePasswordResetToken(
                    passwordRecoveryToken
                )
            ),
            httpStatusOK
        )
    }

    @RequestMapping("user/reset-forgotten-password", method = [RequestMethod.POST])
    fun resetForgottenPassword(
        @RequestParam("token") passwordRecoveryToken: String?,
        @RequestBody request: ForgottenPasswordChangeRequest
    ): ResponseEntity<Map<String, Boolean>> {
        logger.info("Forgotten password reset request")
        val isPasswordTokenValid =
            PasswordResetService(passwordTokenRepository).validatePasswordResetToken(passwordRecoveryToken)

        if (!isPasswordTokenValid) {
            throw PreconditionFailedException("Password reset token is not valid")
        }

        try {
            PasswordChangeValidator.validate(request)
        } catch (e: ValidationException) {
            throw PreconditionFailedException("Validation failed: ${e.message}", e)
        }

        var passwordToken = passwordTokenRepository.findFirstByTokenAndDeleteTimeIsNull(passwordRecoveryToken)
            ?: throw NotFoundException("Password reset token not found")

        var user = passwordToken.user ?: throw NotFoundException("User not found")

        user = user.also {
            it.password = bCryptPasswordEncoder().encode(request.newPassword)
        }

        passwordToken = passwordToken.also {
            it.deleteTime = LocalDateTime.now()
        }

        userRepository.save(user)
        passwordTokenRepository.save(passwordToken)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(User) User password recovered")

        return ResponseEntity(
            mapOf("ok" to true),
            httpStatusOK
        )
    }
}
