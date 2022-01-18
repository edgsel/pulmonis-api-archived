package com.pulmonis.pulmonisapi.controller.exception

open class PreconditionFailedException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable) : super(message, cause)
}
