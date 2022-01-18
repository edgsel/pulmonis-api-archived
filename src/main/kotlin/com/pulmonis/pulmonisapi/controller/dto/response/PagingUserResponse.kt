package com.pulmonis.pulmonisapi.controller.dto.response

class PagingUserResponse {
    var totalItems: Long? = null
    var users: List<UserResponse>? = mutableListOf()
    var totalPages: Long? = null
    var currentPage: Long? = null
}
