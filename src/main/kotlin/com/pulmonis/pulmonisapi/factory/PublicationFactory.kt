package com.pulmonis.pulmonisapi.factory

import com.pulmonis.pulmonisapi.controller.exception.PreconditionFailedException
import com.pulmonis.pulmonisapi.controller.dto.request.PublicationRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.FullPublicationResponse
import com.pulmonis.pulmonisapi.controller.dto.response.PagingFullPublicationResponse
import com.pulmonis.pulmonisapi.controller.dto.response.PagingPublicationResponse
import com.pulmonis.pulmonisapi.controller.dto.response.PublicationResponse
import com.pulmonis.pulmonisapi.hibernate.entities.Publication
import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page

object PublicationFactory {
    fun buildPublication(publication: PublicationRequestBody, user: User): Publication {
        return Publication().also {
            it.titleEn = publication.titleEn
            it.titleEt = publication.titleEt
            it.titleRu = publication.titleRu
            it.descriptionEn = publication.descriptionEn
            it.descriptionEt = publication.descriptionEt
            it.descriptionRu = publication.descriptionRu
            it.bodyEn = publication.bodyEn
            it.bodyEt = publication.bodyEt
            it.bodyRu = publication.bodyRu
            it.category = publication.category
            it.user = user
            it.publishedEn = publication.publishedEn
            it.publishedEt = publication.publishedEt
            it.publishedRu = publication.publishedRu
        }
    }

    fun buildPublicationResponse(publication: Publication): FullPublicationResponse {
        return FullPublicationResponse().also {
            it.id = publication.id
            it.titleEn = publication.titleEn
            it.titleEt = publication.titleEt
            it.titleRu = publication.titleRu
            it.descriptionEn = publication.descriptionEn
            it.descriptionEt = publication.descriptionEt
            it.descriptionRu = publication.descriptionRu
            it.bodyEn = publication.bodyEn
            it.bodyEt = publication.bodyEt
            it.bodyRu = publication.bodyRu
            it.category = publication.category
            it.publishedEn = publication.publishedEn
            it.publishedEt = publication.publishedEt
            it.publishedRu = publication.publishedRu
            it.publishTime = publication.publishTime
        }
    }

    fun buildPageablePublicationResponse(publications: Page<Publication>, lang: String): PagingPublicationResponse {
        return PagingPublicationResponse().also {
            it.currentPage = publications.number.toLong()
            it.publications = publications.content.map { publication ->
                buildSingleLanguagePublicationResponse(publication, lang)
            }
            it.totalItems = publications.totalElements
            it.totalPages = publications.totalPages.toLong()
        }
    }

    fun buildPageableFullPublicationResponse(publications: Page<Publication>): PagingFullPublicationResponse {
        return PagingFullPublicationResponse().also {
            it.currentPage = publications.number.toLong()
            it.publications = publications.content.map { publication ->
                buildPublicationResponse(publication)
            }
            it.totalItems = publications.totalElements
            it.totalPages = publications.totalPages.toLong()
        }
    }

    fun buildSingleLanguagePublicationResponse(publication: Publication, lang: String): PublicationResponse {
        val response = PublicationResponse().also {
            it.id = publication.id
            it.category = publication.category
            it.publishTime = publication.publishTime
        }

        when (lang) {
            "publishedEt" -> return response.also {
                it.title = publication.titleEt
                it.description = publication.descriptionEt
                it.body = publication.bodyEt
                it.published = publication.publishedEt
            }
            "publishedEn" -> return response.also {
                it.title = publication.titleEn
                it.description = publication.descriptionEn
                it.body = publication.bodyEn
                it.published = publication.publishedEn
            }
            "publishedRu" -> return response.also {
                it.title = publication.titleRu
                it.description = publication.descriptionRu
                it.body = publication.bodyRu
                it.published = publication.publishedRu
            }
            else -> throw PreconditionFailedException("Language not supported")
        }
    }

}
