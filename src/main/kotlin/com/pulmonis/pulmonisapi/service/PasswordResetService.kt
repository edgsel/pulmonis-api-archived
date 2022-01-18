package com.pulmonis.pulmonisapi.service

import com.pulmonis.pulmonisapi.hibernate.entities.PasswordResetToken
import com.pulmonis.pulmonisapi.hibernate.entities.PasswordResetToken.Companion.EXPIRATION
import com.pulmonis.pulmonisapi.hibernate.entities.User
import com.pulmonis.pulmonisapi.hibernate.repository.PasswordTokenRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class PasswordResetService(
    private val passwordTokenRepository: PasswordTokenRepository
) {

    fun buildPasswordResetToken(user: User?): String {
        val passwordResetToken = PasswordResetToken().also {
            it.token = UUID.randomUUID().toString()
            it.user = user
            it.expiryTime = LocalDateTime.now().plusHours(EXPIRATION)
        }

        this.passwordTokenRepository.save(passwordResetToken)
        return passwordResetToken.token!!
    }

    fun validatePasswordResetToken(token: String?): Boolean {
        token ?: return false
        val passToken = passwordTokenRepository.findFirstByTokenAndDeleteTimeIsNull(token)
            ?: return false

        return !isTokenExpired(passToken)
    }

    private fun isTokenExpired(passwordResetToken: PasswordResetToken): Boolean {
        return if (passwordResetToken.expiryTime != null) {
            passwordResetToken.expiryTime!!.isBefore(LocalDateTime.now())
        } else {
            true
        }
    }
}
