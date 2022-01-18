package com.pulmonis.pulmonisapi.hibernate.specifications

import com.pulmonis.pulmonisapi.hibernate.entities.Schooling
import com.pulmonis.pulmonisapi.hibernate.repository.SchoolingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class PublicSchoolingSpecification(
    private val schoolingRepository: SchoolingRepository
) {
    fun findSchoolingsByCriteria(
        currentSchooling: Schooling? = null,
        pageRequest: PageRequest
    ): Page<Schooling> {
        val specification = Specification.where<Schooling> { root, _, criteriaBuilder ->
            if (currentSchooling != null) {
                criteriaBuilder.and(
                    criteriaBuilder.notEqual(root.get<Long>("id"), currentSchooling.id),
                    criteriaBuilder.greaterThan(root.get("eventDateTime"), LocalDateTime.now()),
                    criteriaBuilder.greaterThan(root.get("registrationDeadline"), LocalDateTime.now()),
                    criteriaBuilder.isTrue(root.get("published")),
                    root.get<LocalDateTime>("deleteTime").isNull
                )
            } else {
                criteriaBuilder.and(
                    criteriaBuilder.isTrue(root.get("published")),
                    root.get<LocalDateTime>("deleteTime").isNull
                )
            }
        }
        return schoolingRepository.findAll(specification, pageRequest)
    }
}

