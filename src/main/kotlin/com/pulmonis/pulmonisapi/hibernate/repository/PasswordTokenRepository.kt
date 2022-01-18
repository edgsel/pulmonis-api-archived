package com.pulmonis.pulmonisapi.hibernate.repository

import com.pulmonis.pulmonisapi.hibernate.entities.PasswordResetToken
import org.springframework.data.repository.CrudRepository

interface PasswordTokenRepository : CrudRepository<PasswordResetToken, Long> {
    fun findFirstByTokenAndDeleteTimeIsNull(token: String?): PasswordResetToken?
}
