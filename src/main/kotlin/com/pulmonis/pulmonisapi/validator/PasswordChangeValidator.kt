package com.pulmonis.pulmonisapi.validator

import com.pulmonis.pulmonisapi.controller.dto.request.ForgottenPasswordChangeRequest
import com.pulmonis.pulmonisapi.controller.dto.request.PasswordChangeRequest
import com.pulmonis.pulmonisapi.exception.ValidationException

object PasswordChangeValidator {
    @Throws(ValidationException::class)
    fun validate(request: PasswordChangeRequest) {
        when {
            request.oldPassword.isNullOrEmpty() -> throw ValidationException("Old password is empty")
            request.newPassword.isNullOrEmpty() -> throw ValidationException("New password is empty")
            request.newPassword.length < 8 -> throw ValidationException("New password length is less than 8 characters")
            request.newPassword == request.oldPassword -> throw ValidationException("New password cannot be the old password")
        }
    }

    @Throws(ValidationException::class)
    fun validate(request: ForgottenPasswordChangeRequest) {
        when {
            request.newPassword.isNullOrEmpty() -> throw ValidationException("New password is empty")
            request.newPassword.length < 8 -> throw ValidationException("New password length is less than 8 characters")
        }
    }
}
