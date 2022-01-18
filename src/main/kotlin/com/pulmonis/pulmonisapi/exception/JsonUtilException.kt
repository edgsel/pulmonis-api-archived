package com.pulmonis.pulmonisapi.exception

class JsonUtilException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
