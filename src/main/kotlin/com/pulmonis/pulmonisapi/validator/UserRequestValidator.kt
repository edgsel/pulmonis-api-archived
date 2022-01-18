package com.pulmonis.pulmonisapi.validator

import com.pulmonis.pulmonisapi.controller.dto.request.UserRequestBody
import com.pulmonis.pulmonisapi.exception.ValidationException
import java.util.regex.Pattern

object UserRequestValidator {
    @Throws(ValidationException::class)
    fun validate(userRequest: UserRequestBody) {
        when {
            userRequest.email.isNullOrEmpty() || !userRequest.email!!.isEmailValid() ->
                throw ValidationException("Wrong email")
            userRequest.password.isNullOrEmpty() ->
                throw ValidationException("Password is empty")
            userRequest.firstName.isNullOrEmpty() ->
                throw ValidationException("First name is empty")
            userRequest.lastName.isNullOrEmpty() ->
                throw ValidationException("Last name is empty")
        }
    }

    fun String.isEmailValid() =
        Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        ).matcher(this).matches()
}
