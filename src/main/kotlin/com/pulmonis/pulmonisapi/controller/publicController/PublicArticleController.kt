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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/articles")
class PublicArticleController(
    private val publicPublicationSpecification: PublicationSpecification
) {
    private val logger = LoggerFactory.getLogger(PublicArticleController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("", method = [RequestMethod.GET])
    fun getAllArticles(
        @RequestParam("lang") requestLanguage: String,
        @RequestParam(defaultValue = "0") page: Int,
    ): ResponseEntity<PagingPublicationResponse> {
        if (requestLanguage.isEmpty()) {
            throw PreconditionFailedException("Language of the article is not specified")
        }
        val publishedLanguage = PublicationLanguageUtil.getPublishedLanguage(requestLanguage)
            ?: throw NotFoundException("Language '$requestLanguage' is not supported")

        val paging = try {
            ConstUtil.run {
                PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "publishTime"))
            }
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val articleListing = publicPublicationSpecification.findPublicationsByLanguage(
            publishedLanguage,
            ContentType.article,
            paging
        )

        val response = PublicationFactory.buildPageablePublicationResponse(articleListing, publishedLanguage)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) Articles listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/{articleId}", method = [RequestMethod.GET])
    fun getArticle(
        @PathVariable("articleId") articleId: Long,
        @RequestParam("lang") requestLanguage: String
    ): ResponseEntity<PublicationResponse> {
        if (requestLanguage.isEmpty()) {
            throw PreconditionFailedException("Language of the article is not specified")
        }
        val publishedLanguage = PublicationLanguageUtil.getPublishedLanguage(requestLanguage)
            ?: throw NotFoundException("Language '$requestLanguage' is not supported")

        val publishedArticle = try {
            publicPublicationSpecification.findPublicationByLanguageAndId(
                publishedLanguage,
                ContentType.article,
                articleId
            )
        } catch (e: SpecificationException) {
            throw NotFoundException("Article ${e.message}", e)
        }

        val response = PublicationFactory.buildSingleLanguagePublicationResponse(publishedArticle, publishedLanguage)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Public) Article returned")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }
}
