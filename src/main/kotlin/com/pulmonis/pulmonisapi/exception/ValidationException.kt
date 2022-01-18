package com.pulmonis.pulmonisapi.exception

class ValidationException : Exception {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
