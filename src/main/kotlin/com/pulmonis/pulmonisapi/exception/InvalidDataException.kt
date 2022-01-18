package com.pulmonis.pulmonisapi.exception

import java.lang.RuntimeException

class InvalidDataException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable) : super(message, cause)
}
