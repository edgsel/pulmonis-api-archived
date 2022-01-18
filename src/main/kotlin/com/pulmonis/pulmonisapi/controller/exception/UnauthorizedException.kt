package com.pulmonis.pulmonisapi.controller.exception

open class UnauthorizedException: RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable) : super(message, cause)
}
