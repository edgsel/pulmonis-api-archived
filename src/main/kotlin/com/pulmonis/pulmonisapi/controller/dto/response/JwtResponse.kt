package com.pulmonis.pulmonisapi.controller.dto.response

import java.io.Serializable

class JwtResponse : Serializable {
    var token: String? = null

    companion object {
        private const val serialVersionUID = -8091879091924046844L
    }
}
