package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class PublicationRequestBody {
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
}
