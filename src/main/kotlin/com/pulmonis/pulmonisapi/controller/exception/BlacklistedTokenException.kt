package com.pulmonis.pulmonisapi.controller.exception

class BlacklistedTokenException: RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
