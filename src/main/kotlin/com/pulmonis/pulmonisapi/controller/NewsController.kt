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
import io.sentry.Sentry
import java.io.IOException
import com.pulmonis.pulmonisapi.validator.PublicationValidator
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
@RequestMapping("/news")
class NewsController(
    private val jwtTokenUtil: JwtTokenUtil,
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(NewsController::class.java)
    private val httpStatusOK = HttpStatus.OK.value()

    @RequestMapping("/listing", method = [RequestMethod.GET])
    fun getAllNews(
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

        val newsListing = publicationRepository.findNews(user, paging)
        val response = PublicationFactory.buildPageableFullPublicationResponse(newsListing)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) News listed")

        return ResponseEntity(
            response,
            httpStatusOK
        )
    }

    @RequestMapping("/{newsId}", method = [RequestMethod.GET])
    fun getNews(
        @RequestHeader("Authorization") token: String,
        @PathVariable("newsId") newsId: Long
    ): ResponseEntity<FullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val news = publicationRepository.findNews(newsId, user)
            ?: throw NotFoundException("news with id $newsId not found")

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) News piece returned")

        return ResponseEntity(
            PublicationFactory.buildPublicationResponse(news),
            httpStatusOK
        )
    }

    @RequestMapping("/create", method = [RequestMethod.POST])
    fun addNews(
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

        val buildNews = PublicationFactory.buildPublication(request, user)
        buildNews.contentType = ContentType.news

        PublicationService.updatePublishTime(buildNews)
        val savedNews = publicationRepository.save(buildNews)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) News piece created")

        return ResponseEntity(
            PublicationFactory.buildPublicationResponse(savedNews),
            httpStatusOK
        )
    }

    @RequestMapping("/edit/{newsId}", method = [RequestMethod.PATCH])
    fun editNews(
        @RequestHeader("Authorization") token: String,
        @PathVariable("newsId") newsId: Long,
        request: HttpServletRequest
    ): ResponseEntity<FullPublicationResponse> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val existingNews = publicationRepository.findNews(newsId, user)
            ?: throw NotFoundException("news with id $newsId not found")

        val existingIsPublished = existingNews.publishedEn!! || existingNews.publishedEt!! || existingNews.publishedRu!!

        val incomingUpdates = try {
            objectMapper.readerForUpdating(existingNews).readValue(request.reader, Publication::class.java)
        } catch (e: IOException) {
            throw PreconditionFailedException("Failed to process request body: ${e.message}")
        }

        if (!existingIsPublished)
            PublicationService.updatePublishTime(incomingUpdates)

        val updatedNews = publicationRepository.saveAndFlush(incomingUpdates)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) News piece edited")

        return ResponseEntity(
            PublicationFactory.buildPublicationResponse(updatedNews),
            httpStatusOK
        )
    }

    @RequestMapping("/remove/{newsId}", method = [RequestMethod.DELETE])
    fun removeNews(
        @RequestHeader("Authorization") token: String,
        @PathVariable newsId: Long
    ): ResponseEntity<Long> {
        val userIdFromToken = jwtTokenUtil.getUserIdFromToken(token)
            ?: throw BadRequestException("Could not retrieve user id from token")
        val user = userRepository.findFirstById(userIdFromToken)
            ?: throw NotFoundException("User with email $userIdFromToken not found")

        val news = publicationRepository.findNews(newsId, user)
            ?: throw NotFoundException("news with id $newsId not found")

        news.deleteTime = LocalDateTime.now()
        publicationRepository.save(news)

        Sentry.addBreadcrumb("Status: $httpStatusOK")
        logger.info("(Admin) News piece removed")

        return ResponseEntity(
            newsId,
            httpStatusOK
        )
    }

}
