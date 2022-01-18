package com.pulmonis.pulmonisapi.factory

import com.pulmonis.pulmonisapi.hibernate.entities.JwtBlacklist

object JwtBlacklistFactory {
    fun build(token: String): JwtBlacklist {
        return JwtBlacklist().also {
            it.token = token
        }
    }
}
