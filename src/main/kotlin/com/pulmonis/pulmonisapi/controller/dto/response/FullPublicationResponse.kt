package com.pulmonis.pulmonisapi.controller.dto.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
class FullPublicationResponse {
    var id: Long? = null
    var titleEt: String? = null
    var titleEn: String? = null
    var titleRu: String? = null
    var descriptionEt: String? = null
    var descriptionEn: String? = null
    var descriptionRu: String? = null
    var bodyEt: String? = null
    var bodyEn: String? = null
    var bodyRu: String? = null
    var publishedEt: Boolean? = false
    var publishedEn: Boolean? = false
    var publishedRu: Boolean? = false
    var category: String? = null
    var publishTime: LocalDateTime? = null
}
