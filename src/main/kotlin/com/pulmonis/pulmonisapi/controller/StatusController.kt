package com.pulmonis.pulmonisapi.controller

import com.pulmonis.pulmonisapi.rest.ResponseEntity
import io.sentry.Sentry
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory

@RestController
class StatusController {
    private val logger = LoggerFactory.getLogger(StatusController::class.java)
    private val httpStatus = HttpStatus.OK.value()

    @RequestMapping("/status", method = [RequestMethod.GET])
    fun getStatus(): ResponseEntity<Map<String, Any>> {
        Sentry.addBreadcrumb("Status: $httpStatus")
        logger.info("GET status")

        return ResponseEntity(
            mapOf(
                "ok" to true
            ),
            httpStatus
        )
    }
}
