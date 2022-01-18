package com.pulmonis.pulmonisapi.mail.model

class Mail {
    var from: String? = null
    var to: String? = null
    var subject: String? = null
    var props: Map<String, Any>? = mapOf()
}
