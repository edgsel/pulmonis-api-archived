package com.pulmonis.pulmonisapi.controller.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
class PublicationResponse {
    var id: Long? = null
    var title: String? = null
    var description: String? = null
    var body: String? = null
    var published: Boolean? = false
    var category: String? = null
    var publishTime: LocalDateTime? = null
}
