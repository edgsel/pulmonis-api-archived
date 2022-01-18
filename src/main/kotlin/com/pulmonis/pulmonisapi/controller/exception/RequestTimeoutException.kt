package com.pulmonis.pulmonisapi.controller.exception

open class RequestTimeoutException: RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
