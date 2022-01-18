package com.pulmonis.pulmonisapi.controller.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class PasswordChangeRequest {
    val oldPassword: String? = null
    val newPassword: String? = null
}
