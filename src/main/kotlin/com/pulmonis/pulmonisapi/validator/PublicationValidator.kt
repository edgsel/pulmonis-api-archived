package com.pulmonis.pulmonisapi.validator

import com.pulmonis.pulmonisapi.controller.dto.request.PublicationRequestBody
import com.pulmonis.pulmonisapi.exception.ValidationException

object PublicationValidator {
    @Throws(ValidationException::class)
    fun validate(requestBody: PublicationRequestBody) {
        when {
            requestBody.category.isNullOrEmpty() -> throw ValidationException("Category is empty")
        }
    }
}
