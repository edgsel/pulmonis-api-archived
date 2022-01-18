package com.pulmonis.pulmonisapi.hibernate.repository

import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long>, JpaSpecificationExecutor<User>, JpaRepository<User, Long> {
    @Query("SELECT u FROM user u WHERE u.email = ?1 AND u.deleteTime IS NULL")
    fun findByEmail(email: String?): User?

    @Query("SELECT u FROM user u WHERE u.id = ?1 AND u.deleteTime IS NULL")
    fun findFirstById(userId: Long?): User?

    @Query("SELECT u FROM user u WHERE u.isAdmin = FALSE AND u.deleteTime IS NULL")
    fun findAllUsers(paging: Pageable): Page<User>
}
