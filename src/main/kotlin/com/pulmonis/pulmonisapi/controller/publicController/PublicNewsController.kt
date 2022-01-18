package com.pulmonis.pulmonisapi.controller.publicController

import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.dto.response.PagingPublicationResponse
import com.pulmonis.pulmonisapi.controller.dto.response.PublicationResponse
import com.pulmonis.pulmonisapi.exception.SpecificationException
import com.pulmonis.pulmonisapi.factory.PublicationFactory
import com.pulmonis.pulmonisapi.enums.ContentType
import com.pulmonis.pulmonisapi.hibernate.specifications.PublicationSpecification
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.util.ConstUtil
import com.pulmonis.pulmonisapi.util.PublicationLanguageUtil
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
@RequestMapping("/public/news")
class PublicNewsController(
    private val publicPublicationSpecification: PublicationSpecification
) {
    private val logger = LoggerFactory.getLogger(PublicNewsController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("", method = [RequestMethod.GET])
    fun getAllNews(
        @RequestHeader("find-latest") findLatest: Boolean = false,
        @RequestParam("lang") requestLanguage: String,
        @RequestParam(defaultValue = "0") page: Int = 0,
    ): ResponseEntity<PagingPublicationResponse> {
        if (requestLanguage.isEmpty()) {
            throw PreconditionFailedException("Language of news is not specified")
        }
        val publishedLanguage = PublicationLanguageUtil.getPublishedLanguage(requestLanguage)
            ?: throw NotFoundException("Language '$requestLanguage' is not supported")

        val paging = try {
            val (desiredPage, pageSize) = when (findLatest) {
                true -> Pair(0, 4)
                false -> Pair(page, ConstUtil.PAGE_SIZE)
            }
            PageRequest.of(desiredPage, pageSize, Sort.by(Sort.Direction.DESC, "publishTime"))
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val newsListing = publicPublicationSpecification.findPublicationsByLanguage(
            publishedLanguage,
            ContentType.news,
            paging
        )

        val response = PublicationFactory.buildPageablePublicationResponse(newsListing, publishedLanguage)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) News listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/{newsId}", method = [RequestMethod.GET])
    fun getNews(
        @PathVariable("newsId") newsId: Long,
        @RequestParam("lang") requestLanguage: String
    ): ResponseEntity<PublicationResponse> {
        if (requestLanguage.isEmpty()) {
            throw PreconditionFailedException("Language of news is not specified")
        }
        val publishedLanguage = PublicationLanguageUtil.getPublishedLanguage(requestLanguage)
            ?: throw NotFoundException("Language '$requestLanguage' is not supported")

        val publishedNews = try {
            publicPublicationSpecification.findPublicationByLanguageAndId(
                publishedLanguage,
                ContentType.news,
                newsId
            )
        } catch (e: SpecificationException) {
            throw NotFoundException("News ${e.message}", e)
        }

        val response = PublicationFactory.buildSingleLanguagePublicationResponse(publishedNews, publishedLanguage)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) News piece returned")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }
}
