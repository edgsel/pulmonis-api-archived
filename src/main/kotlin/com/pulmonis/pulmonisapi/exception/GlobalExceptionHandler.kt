package com.pulmonis.pulmonisapi.exception

import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.BlacklistedTokenException
import com.pulmonis.pulmonisapi.controller.exception.ForbiddenException
import com.pulmonis.pulmonisapi.controller.exception.JwtTokenValidationException
import com.pulmonis.pulmonisapi.controller.exception.MethodNotAllowedException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.exception.RequestTimeoutException
import com.pulmonis.pulmonisapi.controller.exception.UnauthorizedException
import com.pulmonis.pulmonisapi.rest.ErrorMessage
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun badRequestException(ex: BadRequestException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.BAD_REQUEST.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "BAD_REQUEST")),
            httpStatus)
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun notFoundException(ex: NotFoundException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.NOT_FOUND.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "NOT_FOUND")),
            httpStatus)
    }

    @ExceptionHandler(BlacklistedTokenException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun blacklistedTokenException(ex: BlacklistedTokenException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.UNAUTHORIZED.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "UNAUTHORIZED")),
            httpStatus)
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    fun forbiddenException(ex: ForbiddenException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.FORBIDDEN.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "FORBIDDEN")),
            httpStatus)
    }

    @ExceptionHandler(JwtTokenValidationException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun jwtTokenValidationException(ex: JwtTokenValidationException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.UNAUTHORIZED.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "UNAUTHORIZED")),
            httpStatus)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    fun methodNotAllowedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.METHOD_NOT_ALLOWED.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "METHOD_NOT_ALLOWED")),
            httpStatus)
    }

    @ExceptionHandler(PreconditionFailedException::class)
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    fun preconditionFailedException(ex: PreconditionFailedException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.PRECONDITION_FAILED.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "PRECONDITION_FAILED")),
            httpStatus)
    }

    @ExceptionHandler(RequestTimeoutException::class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    fun requestTimeoutException(ex: RequestTimeoutException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.REQUEST_TIMEOUT.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "REQUEST_TIMEOUT")),
            httpStatus)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun unauthorizedException(ex: UnauthorizedException): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.UNAUTHORIZED.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.warn(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "UNAUTHORIZED")),
            httpStatus)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun unexpectedException(ex: Exception): ResponseEntity<ErrorMessage> {
        val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value()
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.error(ex.message, ex)
        return ResponseEntity(
            null,
            listOf(ErrorMessage(ex.message, "INTERNAL_SERVER_ERROR")),
            httpStatus)
    }
}
