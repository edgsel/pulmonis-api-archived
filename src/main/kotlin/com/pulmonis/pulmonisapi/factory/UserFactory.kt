package com.pulmonis.pulmonisapi.factory

import com.pulmonis.pulmonisapi.controller.dto.request.UserRequestBody
import com.pulmonis.pulmonisapi.controller.dto.response.PagingUserResponse
import com.pulmonis.pulmonisapi.controller.dto.response.UserResponse
import com.pulmonis.pulmonisapi.enums.UserStatus
import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page

object UserFactory {
    fun build(user: UserRequestBody): User {
        return User().also {
            it.email = user.email
            it.password = user.password
            it.firstName = user.firstName
            it.lastName = user.lastName
            it.isAdmin = user.isAdmin
            it.status = UserStatus.pending
        }
    }

    fun buildUserResponse(user: User): UserResponse {
        return UserResponse().also {
            it.email = user.email
            it.status = user.status
        }
    }

    fun buildPagingUserResponse(users: Page<User>): PagingUserResponse {
        return PagingUserResponse().also {
            it.currentPage = users.number.toLong()
            it.users = users.content.map { user ->
                buildUserResponse(user)
            }
            it.totalItems = users.totalElements
            it.totalPages = users.totalPages.toLong()
        }
    }
}
