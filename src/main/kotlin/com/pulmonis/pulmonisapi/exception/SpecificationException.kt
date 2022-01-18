package com.pulmonis.pulmonisapi.exception

open class SpecificationException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
