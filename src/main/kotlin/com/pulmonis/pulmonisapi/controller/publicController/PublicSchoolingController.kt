package com.pulmonis.pulmonisapi.controller.publicController

import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.dto.response.PagingSchoolingResponse
import com.pulmonis.pulmonisapi.controller.dto.response.SchoolingResponse
import com.pulmonis.pulmonisapi.factory.SchoolingFactory
import com.pulmonis.pulmonisapi.hibernate.repository.SchoolingRepository
import com.pulmonis.pulmonisapi.hibernate.specifications.PublicSchoolingSpecification
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.util.ConstUtil
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public")
class PublicSchoolingController(
    private val schoolingRepository: SchoolingRepository,
    private val publicSchoolingSpecification: PublicSchoolingSpecification
) {
    private val logger = LoggerFactory.getLogger(PublicNewsController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("/schoolings", method = [RequestMethod.GET])
    fun getAllSchoolings(
        @RequestHeader("find-latest") findLatest: Boolean = false,
        @RequestHeader("schooling-id") schoolingId: Long?,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<PagingSchoolingResponse> {
        val currentlySelectedSchooling = schoolingId?.let {
            schoolingRepository.findSchoolingById(it) ?: throw NotFoundException("Schooling with id $it not found")
        }

        val paging = try {
            val pageSize = when {
                schoolingId != null -> 3
                findLatest -> 2
                else -> ConstUtil.PAGE_SIZE
            }
            PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "eventDateTime"))
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val schoolings = publicSchoolingSpecification.findSchoolingsByCriteria(
            currentlySelectedSchooling,
            paging
        )

        val response = SchoolingFactory.buildPageableSchoolingResponse(schoolings)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) Schoolings listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/schooling/{schoolingId}", method = [RequestMethod.GET])
    fun getSchoolingById(
        @PathVariable schoolingId: String
    ): ResponseEntity<SchoolingResponse> {
        val id = schoolingId.toLongOrNull() ?: throw BadRequestException("Given $schoolingId id contains wrong chars")
        val schooling = schoolingRepository.findSchoolingById(id)
            ?: throw NotFoundException("Schooling with ID $id not found")

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) Schooling returned")

        return ResponseEntity(
            SchoolingFactory.buildSchoolingResponse(schooling),
            httpStatusOK
        )
    }
}

