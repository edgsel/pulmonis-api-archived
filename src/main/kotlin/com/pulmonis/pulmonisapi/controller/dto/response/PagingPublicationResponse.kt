package com.pulmonis.pulmonisapi.controller.dto.response

class PagingPublicationResponse {
    var totalItems: Long? = null
    var publications: List<PublicationResponse>? = listOf()
    var totalPages: Long? = null
    var currentPage: Long? = null
}
