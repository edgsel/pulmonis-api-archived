package com.pulmonis.pulmonisapi.util

object PublicationLanguageUtil {
    private val publishedLanguages = mapOf(
        "et" to "publishedEt",
        "en" to "publishedEn",
        "ru" to "publishedRu"
    )

    fun getPublishedLanguage(language: String): String? {
        return publishedLanguages[language]
    }
}
