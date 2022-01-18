package com.pulmonis.pulmonisapi.hibernate.repository

import com.pulmonis.pulmonisapi.hibernate.entities.JwtBlacklist
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JwtBlacklistRepository : CrudRepository<JwtBlacklist, Long> {
    fun findFirstByToken(token: String): JwtBlacklist?
}
