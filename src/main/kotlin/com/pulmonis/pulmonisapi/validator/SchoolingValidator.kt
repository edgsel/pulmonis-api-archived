package com.pulmonis.pulmonisapi.validator

import com.pulmonis.pulmonisapi.controller.dto.request.SchoolingRequestBody
import com.pulmonis.pulmonisapi.exception.ValidationException
import java.math.BigDecimal

object SchoolingValidator {
    @Throws(ValidationException::class)
    fun validate(requestBody: SchoolingRequestBody) {
        when {
            requestBody.title.isNullOrEmpty() -> throw ValidationException("Title is empty")
            requestBody.description.isNullOrEmpty() -> throw ValidationException("Description is missing")
            requestBody.address.isNullOrEmpty() -> throw ValidationException("Address is missing")
            requestBody.city.isNullOrEmpty() -> throw ValidationException("City is missing")
            requestBody.category.isNullOrEmpty() -> throw ValidationException("Category is missing")
            requestBody.registrationDeadline == null -> throw ValidationException("Registration deadline is empty")
            requestBody.eventDateTime == null -> throw ValidationException("Event date time is empty")
            !requestBody.free!! && (requestBody.price == null || requestBody.price == BigDecimal.ZERO) ->
                throw ValidationException("Schooling is not free, but price is zero or empty")
        }
    }
}
