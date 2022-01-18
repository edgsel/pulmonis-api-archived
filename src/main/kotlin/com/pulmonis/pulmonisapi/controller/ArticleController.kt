package com.pulmonis.pulmonisapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.pulmonis.pulmonisapi.controller.exception.BadRequestException
import com.pulmonis.pulmonisapi.controller.exception.NotFoundException
import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.dto.request.PublicationRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.FullPublicationResponse
import com.pulmonis.pulmonisapi.controller.dto.response.PagingFullPublicationResponse
import com.pulmonis.pulmonisapi.exception.ValidationException
import com.pulmonis.pulmonisapi.factory.PublicationFactory
import com.pulmonis.pulmonisapi.enums.ContentType
import com.pulmonis.pulmonisapi.hibernate.entities.Publication
import com.pulmonis.pulmonisapi.hibernate.repository.PublicationRepository
import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import com.pulmonis.pulmonisapi.rest.ResponseEntity
import com.pulmonis.pulmonisapi.service.PublicationService
import com.pulmonis.pulmonisapi.util.ConstUtil
import com.pulmonis.pulmonisapi.util.JwtTokenUtil
import com.pulmonis.pulmonisapi.validator.PublicationValidator
import io.sentry.Sentry
import java.io.IOException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestParam

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(ArticleController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("/listing", method = [RequestMethod.GET])
    fun getAllArticles(
        @RequestHeader("Authorization") token: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<PagingFullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val paging = try {
            ConstUtil.run {
                PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, PAGE_SORTED_BY))
            }
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Failed to process pagination: ${e.message}", e)
        }

        val articleListing = publicationRepository.findArticles(user, paging)
        val response = PublicationFactory.buildPageableFullPublicationResponse(articleListing)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Articles listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/{articleId}", method = [RequestMethod.GET])
    fun getArticle(
        @RequestHeader("Authorization") token: String,
        @PathVariable("articleId") articleId: Long
    ): ResponseEntity<FullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val articles = publicationRepository.findArticle(articleId, user)
            ?: throw NotFoundException("Article with id $articleId not found")

        val response = PublicationFactory.buildPublicationResponse(articles)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Article returned")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/create", method = [RequestMethod.POST])
    fun addArticle(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: PublicationRequestBody
    ): ResponseEntity<FullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        try {
            PublicationValidator.validate(request)
        } catch (e: ValidationException) {
            throw PreconditionFailedException("Validation failed: ${e.message}", e)
        }

        val buildArticle = PublicationFactory.buildPublication(request, user)
        buildArticle.contentType = ContentType.article

        PublicationService.updatePublishTime(buildArticle)
        val savedArticle = publicationRepository.save(buildArticle)

        val response = PublicationFactory.buildPublicationResponse(savedArticle)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Article created")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/edit/{articleId}", method = [RequestMethod.PATCH])
    fun editArticle(
        @RequestHeader("Authorization") token: String,
        @PathVariable("articleId") articleId: Long,
        request: HttpServletRequest
    ): ResponseEntity<FullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val existingArticle = publicationRepository.findArticle(articleId, user)
            ?: throw NotFoundException("Article with id $articleId not found")

        val existingIsPublished =
            existingArticle.publishedEn!! || existingArticle.publishedEt!! || existingArticle.publishedRu!!

        val incomingUpdates = try {
            objectMapper.readerForUpdating(existingArticle).readValue(request.reader, Publication::class.java)
        } catch (e: IOException) {
            throw PreconditionFailedException("Failed to process request body: ${e.message}")
        }

        if (!existingIsPublished)
            PublicationService.updatePublishTime(incomingUpdates)

        val updatedArticle = publicationRepository.saveAndFlush(incomingUpdates)

        val response = PublicationFactory.buildPublicationResponse(updatedArticle)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Article edited")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/remove/{articleId}", method = [RequestMethod.DELETE])
    fun removeArticle(
        @RequestHeader("Authorization") token: String,
        @PathVariable articleId: Long
    ): ResponseEntity<Long> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val article = publicationRepository.findArticle(articleId, user)
            ?: throw NotFoundException("Article with id $articleId not found")

        article.deleteTime = LocalDateTime.now()
        publicationRepository.save(article)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) Article removed")

        return ResponseEntity(
            articleId,
            httpStatusOK
        )
    }

}
