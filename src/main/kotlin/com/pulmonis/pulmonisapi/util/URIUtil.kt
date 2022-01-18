package com.pulmonis.pulmonisapi.util

import com.pulmonis.pulmonisapi.Environment.ADMIN_WEBAPP_URL

object URIUtil {
    fun buildPasswordResetUri(token: String): String {
        return "$ADMIN_WEBAPP_URL/reset-forgotten-password?token=$token"
    }
}
