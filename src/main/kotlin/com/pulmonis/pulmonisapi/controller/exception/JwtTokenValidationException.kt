package com.pulmonis.pulmonisapi.controller.exception

class JwtTokenValidationException: RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
