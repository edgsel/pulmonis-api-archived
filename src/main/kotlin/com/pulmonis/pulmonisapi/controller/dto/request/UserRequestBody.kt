package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class UserRequestBody {
    var email: String? = null
    var password: String? = null
    var firstName: String? = null
    var lastName: String? = null

    @get:JsonProperty("isAdmin")
    var isAdmin: Boolean? = false
}
