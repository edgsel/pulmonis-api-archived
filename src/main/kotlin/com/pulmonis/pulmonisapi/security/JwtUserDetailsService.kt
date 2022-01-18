package com.pulmonis.pulmonisapi.security

import com.pulmonis.pulmonisapi.hibernate.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import com.pulmonis.pulmonisapi.hibernate.entities.User as HibernateUser


@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    @Autowired
    private val bcryptEncoder: PasswordEncoder? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User with username: $username not found")

        return User(user.email, user.password, listOf())
    }

    fun save(user: HibernateUser): HibernateUser {
        val newUser = HibernateUser().also {
            it.email = user.email
            it.password = bcryptEncoder?.encode(user.password)
            it.firstName = user.firstName
            it.lastName = user.lastName
            it.isAdmin = user.isAdmin
            it.status = user.status
        }

        return userRepository.save(newUser)
    }
}
