package com.pulmonis.pulmonisapi.controller.exception

open class MethodNotAllowedException: RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
