package com.pulmonis.pulmonisapi.hibernate.specifications

import com.pulmonis.pulmonisapi.exception.SpecificationException
import com.pulmonis.pulmonisapi.enums.ContentType
import com.pulmonis.pulmonisapi.hibernate.entities.Publication
import com.pulmonis.pulmonisapi.hibernate.repository.PublicationRepository
import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class PublicationSpecification(
    private val publicationRepository: PublicationRepository
) {
    fun findPublicationsByLanguage(publishedLanguage: String?, contentType: ContentType?, paging: Pageable): Page<Publication> {
        val specification = Specification.where<Publication> { root, _, criteriaBuilder ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get<Boolean>(publishedLanguage), true),
                root.get<LocalDateTime>("deleteTime").isNull,
                criteriaBuilder.equal(root.get<String>("contentType"), contentType)
            )
        }
        return publicationRepository.findAll(specification, paging)
    }

    @Throws(SpecificationException::class)
    fun findPublicationByLanguageAndId(publishedLanguage: String?, contentType: ContentType?, id: Long): Publication {
        val specification = Specification.where<Publication> { root, _, criteriaBuilder ->
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get<Boolean>(publishedLanguage), true),
                root.get<LocalDateTime>("deleteTime").isNull,
                criteriaBuilder.equal(root.get<String>("contentType"), contentType),
                criteriaBuilder.equal(root.get<Long>("id"), id)
            )
        }

        return publicationRepository.findOne(specification).orElseThrow {
            throw SpecificationException("publication with id $id not found")
        }
    }
}
