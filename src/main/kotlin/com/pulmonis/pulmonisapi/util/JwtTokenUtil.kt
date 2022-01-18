package com.pulmonis.pulmonisapi.util

import com.pulmonis.pulmonisapi.controller.exception.JwtTokenValidationException
import com.pulmonis.pulmonisapi.exception.ValidationException
import com.pulmonis.pulmonisapi.hibernate.entities.User
import com.pulmonis.pulmonisapi.hibernate.repository.JwtBlacklistRepository
import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import java.io.Serializable
import java.util.Date
import java.util.function.Function
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class JwtTokenUtil(
    private val jwtBlacklistRepository: JwtBlacklistRepository,
    private val userRepository: UserRepository
) : Serializable {

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expirationDateInMs}")
    private val expirationDateInMs: Long? = null

    @Value("\${jwt.refreshExpirationDateInMs}")
    private val refreshExpirationDateInMs: Long? = null

    fun getUsernameFromToken(token: String?): String? {
        return getClaimFromToken(token, Function { obj: Claims -> obj.subject })
    }

    @Throws(JwtTokenValidationException::class)
    fun getUserIdFromToken(token: String?): Long? {
        val formattedToken = try {
            formatToken(token)
        } catch (e: MalformedJwtException) {
            throw JwtTokenValidationException("Failed to format JWT token", e)
        }

        val claimsFromToken = getAllClaimsFromToken(formattedToken)
        val userIdFromToken = claimsFromToken["userId"]

        return if (userIdFromToken is Int) {
            userIdFromToken.toLong()
        } else {
            null
        }
    }

    fun getIssuedAtDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Function { obj: Claims -> obj.issuedAt })
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token, Function { obj: Claims -> obj.expiration })
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    @Throws(JwtTokenValidationException::class)
    fun getAllClaimsFromToken(token: String?): Claims {
        return try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        } catch (e: MalformedJwtException) {
            throw JwtTokenValidationException("Failed to validate access token", e)
        } catch (e: SignatureException) {
            throw JwtTokenValidationException("Token signature does not match", e)
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails, user: User): String {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, userDetails.username, user)
    }

    private fun doGenerateToken(
        claims: Map<String, Any>,
        username: String,
        user: User
    ): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .claim("userId", user.id)
            .claim("isAdmin", user.isAdmin)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expirationDateInMs!!))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun doGenerateRefreshToken(
        claims: Map<String, Any>,
        username: String,
        user: User
    ): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .claim("userId", user.id)
            .claim("isAdmin", user.isAdmin)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + refreshExpirationDateInMs!!))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    @Throws(MalformedJwtException::class)
    fun formatToken(requestTokenHeader: String?): String {
        return if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            requestTokenHeader.substring(7)
        } else {
            throw throw MalformedJwtException("JWT Token does not begin with Bearer String")
        }
    }

    @Throws(ValidationException::class)
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        try {
            if (jwtBlacklistRepository.findFirstByToken(token) != null) {
                throw JwtTokenValidationException("Token is not valid")
            }
        } catch (e: MalformedJwtException) {
            throw JwtTokenValidationException("Token is not correctly constructed", e)
        } catch (e: SignatureException) {
            throw JwtTokenValidationException("Token signature is not valid", e)
        }

        val userIdFromAccessToken = getAllClaimsFromToken(token)["userId"]

        val user = userRepository.findByEmail(userDetails.username)
            ?: throw ValidationException("User with email: ${userDetails.username} is not found")

        if (userIdFromAccessToken.toString() != user.id.toString()) {
            throw ValidationException("Access token validation failed. Username or user id does not match")
        }
        val username = getUsernameFromToken(token)

        return username == userDetails.username && !isTokenExpired(token)
    }

    companion object {
        private const val serialVersionUID = -2550185165626007488L
    }
}
