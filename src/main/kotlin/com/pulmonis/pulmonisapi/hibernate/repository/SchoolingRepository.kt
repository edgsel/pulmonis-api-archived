package com.pulmonis.pulmonisapi.hibernate.repository

import com.pulmonis.pulmonisapi.hibernate.entities.Schooling
import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SchoolingRepository: CrudRepository<Schooling, Long>, JpaSpecificationExecutor<Schooling>, JpaRepository<Schooling, Long> {
    fun findAllByUserIdAndDeleteTimeIsNull(userId: Long, paging: Pageable): Page<Schooling>
    fun findSchoolingByIdAndUserAndDeleteTimeIsNull(schoolingId: Long, user: User): Schooling?

    @Query("SELECT s FROM schooling s WHERE s.id = ?1 AND s.published = TRUE AND s.deleteTime IS NULL")
    fun findSchoolingById(schoolingId: Long): Schooling?
}
