package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class JwtRequest : Serializable {
    var email: String? = null
    var password: String? = null

    companion object {
        private const val serialVersionUID = 5926468583005150707L
    }
}
