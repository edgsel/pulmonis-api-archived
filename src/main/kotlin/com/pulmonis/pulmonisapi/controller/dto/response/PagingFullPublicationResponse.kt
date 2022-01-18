package com.pulmonis.pulmonisapi.controller.dto.response

class PagingFullPublicationResponse {
    var totalItems: Long? = null
    var publications: List<FullPublicationResponse>? = listOf()
    var totalPages: Long? = null
    var currentPage: Long? = null
}
