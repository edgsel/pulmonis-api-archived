package com.pulmonis.pulmonisapi.hibernate.entities

import com.pulmonis.pulmonisapi.hibernate.enums.PostgreSQLEnumType
import com.pulmonis.pulmonisapi.enums.UserStatus
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import org.hibernate.annotations.Where
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "user")
@Where(clause = "delete_time IS NULL")
@TypeDef(name = "statuses", typeClass = PostgreSQLEnumType::class)
class User : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "email")
    var email: String? = null

    @Column(name = "password")
    var password: String? = null

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @Column(name = "is_admin")
    var isAdmin: Boolean? = false

    @Column(name = "create_time", insertable = false, updatable = false)
    var createTime: LocalDateTime? = null

    @Column(name = "update_time", insertable = false, updatable = false)
    var updateTime: LocalDateTime? = null

    @Column(name = "delete_time")
    var deleteTime: LocalDateTime? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Type(type = "statuses")
    var status: UserStatus? = null
}
