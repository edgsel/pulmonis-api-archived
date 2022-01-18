package com.pulmonis.pulmonisapi.controller.dto.response

class PagingSchoolingResponse {
    var totalItems: Long? = null
    var schoolings: List<SchoolingResponse>? = listOf()
    var totalPages: Long? = null
    var currentPage: Long? = null
}
