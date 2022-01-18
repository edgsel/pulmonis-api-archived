package com.pulmonis.pulmonisapi.hibernate.repository

import com.pulmonis.pulmonisapi.hibernate.entities.Publication
import com.pulmonis.pulmonisapi.hibernate.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface PublicationRepository : CrudRepository<Publication, Long>, JpaSpecificationExecutor<Publication>, JpaRepository<Publication, Long> {
    @Query("SELECT n from publication n WHERE n.user = ?1 AND n.deleteTime IS NULL AND n.contentType = 'news'")
    fun findNews(user: User, paging: Pageable): Page<Publication>

    @Query("SELECT n from publication n WHERE n.id = ?1 AND n.user = ?2 AND n.deleteTime IS NULL AND n.contentType = 'news'")
    fun findNews(id: Long, user: User): Publication?

    @Query("SELECT a from publication a WHERE a.id = ?1 AND a.deleteTime IS NULL AND a.contentType = 'article'")
    fun findArticle(id: Long): Publication?

    @Query("SELECT a from publication a WHERE a.user = ?1 AND a.deleteTime IS NULL AND a.contentType = 'article'")
    fun findArticles(user: User, paging: Pageable): Page<Publication>

    @Query("SELECT a from publication a WHERE a.id = ?1 AND a.user = ?2 AND a.deleteTime IS NULL AND a.contentType = 'article'")
    fun findArticle(id: Long, user: User): Publication?
}
