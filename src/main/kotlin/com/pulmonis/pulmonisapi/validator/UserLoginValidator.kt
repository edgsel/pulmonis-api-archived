package com.pulmonis.pulmonisapi.validator

import com.pulmonis.pulmonisapi.controller.dto.request.JwtRequest
import com.pulmonis.pulmonisapi.exception.ValidationException
import com.pulmonis.pulmonisapi.security.bCryptPasswordEncoder

object UserLoginValidator {
    @Throws(ValidationException::class)
    fun validate(user: JwtRequest) {
        when {
            user.email.isNullOrEmpty() ->
                throw ValidationException("Email is empty")
            user.password.isNullOrEmpty() ->
                throw ValidationException("Password is empty")
        }
    }

    fun validateUserPassword(rawPassword: String, hashedPassword: String): Boolean =
        bCryptPasswordEncoder().matches(rawPassword, hashedPassword)
}
