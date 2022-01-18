package com.pulmonis.pulmonisapi.controller

import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.UnauthorizedException
import com.pulmonis.pulmonisapi.controller.dto.request.UserStatusRequest
import com.pulmonis.pulmonisapi.controller.dto.response.PagingUserResponse
import com.pulmonis.pulmonisapi.controller.dto.response.UserResponse
import com.pulmonis.pulmonisapi.factory.UserFactory
import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.util.ConstUtil
import com.pulmonis.pulmonisapi.util.JwtTokenUtil
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userRepository: UserRepository,
    private val jwtTokenUtil: JwtTokenUtil
) {

    private val logger = LoggerFactory.getLogger(SchoolingController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("/listing", method = [RequestMethod.GET])
    fun getAllUsers(
        @RequestHeader("Authorization") token: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<PagingUserResponse> {
        val userId = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userId)
            ?: throw BadRequestException("User with id $userId not found")

        if (!user.isAdmin!!) {
            throw UnauthorizedException("You are not admin")
        }

        val paging = try {
            ConstUtil.run {
                PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, PAGE_SORTED_BY))
            }
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val users = userRepository.findAllUsers(paging)
        val response = UserFactory.buildPagingUserResponse(users)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) users listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/update-status/{email}", method = [RequestMethod.PUT])
    fun editUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable("email") email: String,
        @RequestBody userStatusRequest: UserStatusRequest
    ): ResponseEntity<UserResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw BadRequestException("User with id $userIdFromToken not found")
        if (!user.isAdmin!!) {
            throw BadRequestException("You are not admin")
        }

        if (userStatusRequest.status == null) {
            throw BadRequestException("Status is not specified")
        }

        val existingUser = userRepository.findByEmail(email)
            ?: throw NotFoundException("User with email $email not found")

        existingUser.status = userStatusRequest.status

        val updatedUser = userRepository.save(existingUser)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) User edited")

        return ResponseEntity(
            UserFactory.buildUserResponse(updatedUser),
            httpStatusOK
        )
    }

}
