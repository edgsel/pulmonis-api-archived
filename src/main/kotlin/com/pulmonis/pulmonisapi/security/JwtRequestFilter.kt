package com.pulmonis.pulmonisapi.security

import com.pulmonis.pulmonisapi.controller.exception.JwtTokenValidationException
import com.pulmonis.pulmonisapi.util.JwtTokenUtil
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtRequestFilter(
    private val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {
    @Autowired
    private val jwtUserDetailsService: JwtUserDetailsService? = null

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private val resolver: HandlerExceptionResolver? = null

    @Throws(ServletException::class, IOException::class, JwtTokenValidationException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val requestTokenHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwtToken: String? = null
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null) {
            try {
                jwtToken = jwtTokenUtil.formatToken(requestTokenHeader)
                username = jwtTokenUtil.getUsernameFromToken(jwtToken)
            } catch (e: IllegalArgumentException) {
                resolver?.resolveException(request, response, null, JwtTokenValidationException("Unable to get JWT Token", e))
                return
            } catch (e: ExpiredJwtException) {
                resolver?.resolveException(request, response, null, JwtTokenValidationException("JWT token expired", e))
                return
            } catch (e: MalformedJwtException) {
                resolver?.resolveException(request, response, null, JwtTokenValidationException("Failed to format JWT token", e))
                return
            }
        } else {
            logger.warn("No JWT token provided")
        }

        //Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = jwtUserDetailsService!!.loadUserByUsername(username)

            // if token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken!!, userDetails)) {
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities)
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            } else {
                logger.warn("not valid token")
            }
        }
        chain.doFilter(request, response)
    }
}
