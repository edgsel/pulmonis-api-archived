package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ForgottenPasswordChangeRequest {
    val newPassword: String? = null
}
