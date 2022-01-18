package com.pulmonis.pulmonisapi.service

import com.pulmonis.pulmonisapi.hibernate.entities.Publication
import java.time.LocalDateTime

object PublicationService {
    fun updatePublishTime(publicationObject: Publication) {
        if (publicationObject.publishedEn!! || publicationObject.publishedEt!! || publicationObject.publishedRu!!)
            publicationObject.publishTime = LocalDateTime.now()
    }
}
