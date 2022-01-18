package com.pulmonis.pulmonisapi.hibernate.entities

import com.pulmonis.pulmonisapi.enums.ContentType
import com.pulmonis.pulmonisapi.hibernate.enums.PostgreSQLEnumType
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.FetchType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef

@Entity(name = "publication")
@Where(clause = "delete_time IS NULL")
@TypeDef(name = "types", typeClass = PostgreSQLEnumType::class)
class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "title_ee")
    var titleEt: String? = null

    @Column(name = "title_en")
    var titleEn: String? = null

    @Column(name = "title_ru")
    var titleRu: String? = null

    @Column(name = "description_ee")
    var descriptionEt: String? = null

    @Column(name = "description_en")
    var descriptionEn: String? = null

    @Column(name = "description_ru")
    var descriptionRu: String? = null

    @Column(name = "body_ee")
    var bodyEt: String? = null

    @Column(name = "body_en")
    var bodyEn: String? = null

    @Column(name = "body_ru")
    var bodyRu: String? = null

    @Column(name = "published_ru")
    var publishedRu: Boolean? = false

    @Column(name = "published_en")
    var publishedEn: Boolean? = false

    @Column(name = "published_et")
    var publishedEt: Boolean? = false

    @Column(name = "category")
    var category: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null

    @Column(name = "create_time", insertable = false, updatable = false)
    var createTime: LocalDateTime? = null

    @Column(name = "update_time", insertable = false, updatable = false)
    var updateTime: LocalDateTime? = null

    @Column(name = "delete_time")
    var deleteTime: LocalDateTime? = null

    @Column(name = "publish_time")
    var publishTime: LocalDateTime? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    @Type(type = "types")
    var contentType: ContentType? = null
}
