package com.pulmonis.pulmonisapi.factory

import com.pulmonis.pulmonisapi.controller.dto.request.SchoolingRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.PagingSchoolingResponse
import com.pulmonis.pulmonisapi.controller.dto.response.SchoolingResponse
import com.pulmonis.pulmonisapi.hibernate.entities.Schooling
import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page

object SchoolingFactory {
    fun build(schooling: SchoolingRequestBody, user: User): Schooling {
        return Schooling().also {
            it.title = schooling.title
            it.description = schooling.description
            it.address = schooling.address
            it.city = schooling.city
            it.category = schooling.category
            it.eventDateTime = schooling.eventDateTime
            it.registrationDeadline = schooling.registrationDeadline
            it.user = user
            it.free = schooling.free
            it.price = schooling.price
            it.published = schooling.published
        }
    }

    fun buildPageableSchoolingResponse(schoolings: Page<Schooling>): PagingSchoolingResponse {
        return PagingSchoolingResponse().also {
            it.currentPage = schoolings.number.toLong()
            it.schoolings = schoolings.content.map { schooling ->
                buildSchoolingResponse(schooling)
            }
            it.totalItems = schoolings.totalElements
            it.totalPages = schoolings.totalPages.toLong()
        }
    }

    fun buildSchoolingResponse(schooling: Schooling): SchoolingResponse {
        return SchoolingResponse().also {
            it.id = schooling.id
            it.title = schooling.title
            it.description = schooling.description
            it.address = schooling.address
            it.city = schooling.city
            it.category = schooling.category
            it.eventDateTime = schooling.eventDateTime
            it.registrationDeadline = schooling.registrationDeadline
            it.free = schooling.free
            it.price = schooling.price
            it.published = schooling.published
        }
    }
}
