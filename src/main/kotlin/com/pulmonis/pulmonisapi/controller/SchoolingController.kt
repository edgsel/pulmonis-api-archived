package com.pulmonis.pulmonisapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.dto.request.SchoolingRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.PagingSchoolingResponse
import com.pulmonis.pulmonisapi.controller.dto.response.SchoolingResponse
import com.pulmonis.pulmonisapi.exception.ValidationException
import com.pulmonis.pulmonisapi.factory.SchoolingFactory
import com.pulmonis.pulmonisapi.hibernate.entities.Schooling
import com.pulmonis.pulmonisapi.hibernate.repository.SchoolingRepository
import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.util.ConstUtil
import com.pulmonis.pulmonisapi.util.JwtTokenUtil
import com.pulmonis.pulmonisapi.validator.SchoolingValidator
import java.io.IOException
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import io.sentry.Sentry
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RequestParam
import java.lang.IllegalArgumentException
import org.springframework.data.domain.Sort

@RestController
@RequestMapping("/schooling")
class SchoolingController(
    private val schoolingRepository: SchoolingRepository,
    private val userRepository: UserRepository,
    private val jwtTokenUtil: JwtTokenUtil,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(SchoolingController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("/remove/{schoolingId}", method = [RequestMethod.DELETE])
    fun removeSchooling(
        @RequestHeader("Authorization") token: String,
        @PathVariable schoolingId: Long
    ): ResponseEntity<Long> {
        val userId = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userId)
            ?: throw BadRequestException("User with id $userId not found")
        val schooling = schoolingRepository.findSchoolingByIdAndUserAndDeleteTimeIsNull(schoolingId, user)
            ?: throw NotFoundException("Schooling with ID $schoolingId not found")

        schooling.deleteTime = LocalDateTime.now()
        schoolingRepository.save(schooling)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Schooling removed")

        return ResponseEntity(
            schoolingId,
            httpStatusOK
        )
    }

    @RequestMapping("/listing", method = [RequestMethod.GET])
    fun getAllSchoolings(
        @RequestHeader("Authorization") token: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<PagingSchoolingResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")

        userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with ID $userIdFromToken not found")

        val paging = try {
            ConstUtil.run {
                PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, PAGE_SORTED_BY))
            }
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val pageableSchoolings = schoolingRepository.findAllByUserIdAndDeleteTimeIsNull(userIdFromToken, paging)
        val response = SchoolingFactory.buildPageableSchoolingResponse(pageableSchoolings)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Schoolings listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/create", method = [RequestMethod.POST])
    @Throws(BadRequestException::class)
    fun createSchooling(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: SchoolingRequestBody
    ): ResponseEntity<SchoolingResponse> {
        try {
            SchoolingValidator.validate(request)
        } catch (e: ValidationException) {
            throw PreconditionFailedException("Validation failed: ${e.message}", e)
        }
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")

        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with ID $userIdFromToken not found")

        val newSchooling = SchoolingFactory.build(request, user)
        val savedSchooling = schoolingRepository.save(newSchooling)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Schooling created")

        return ResponseEntity(
            SchoolingFactory.buildSchoolingResponse(savedSchooling),
            httpStatusOK
        )
    }

    @RequestMapping("/{schoolingId}", method = [RequestMethod.GET])
    fun getSchooling(
        @RequestHeader("Authorization") token: String,
        @PathVariable("schoolingId") schoolingId: Long
    ): ResponseEntity<SchoolingResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")

        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val schooling = schoolingRepository.findSchoolingByIdAndUserAndDeleteTimeIsNull(schoolingId, user)
            ?: throw NotFoundException("Schooling with id $schoolingId not found")

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Schooling returned")

        return ResponseEntity(
            SchoolingFactory.buildSchoolingResponse(schooling),
            httpStatusOK
        )
    }

    @RequestMapping("/edit/{schoolingId}", method = [RequestMethod.PATCH])
    fun editSchooling(
        @RequestHeader("Authorization") token: String,
        @PathVariable("schoolingId") schoolingId: Long,
        request: HttpServletRequest
    ): ResponseEntity<SchoolingResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")

        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val existingSchooling = schoolingRepository.findSchoolingByIdAndUserAndDeleteTimeIsNull(schoolingId, user)
            ?: throw NotFoundException("Schooling with id $schoolingId not found")

        val incomingUpdates = try {
            objectMapper.readerForUpdating(existingSchooling).readValue(request.reader, Schooling::class.java)
        } catch (e: IOException) {
            throw PreconditionFailedException("Failed to process request body: ${e.message}")
        }

        val updatedSchooling = schoolingRepository.saveAndFlush(incomingUpdates)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Schooling edited")

        return ResponseEntity(
            SchoolingFactory.buildSchoolingResponse(updatedSchooling),
            httpStatusOK
        )
    }
}
