package com.example.core.remote

sealed class UnifiedError : Exception() {
    data class Generic(override val message: String) : UnifiedError()

    sealed class Http(override val message: String) : UnifiedError() {
        data class Unauthorized(override val message: String) : Http(message = message)
        data class NotFound(override val message: String) : Http(message = message)
        data class InternalError(override val message: String) : Http(message = message)
        data class BadRequest(override val message: String) : Http(message = message)
    }

    sealed class Connectivity(messageResource: Int) : UnifiedError() {
        data class HostUnreachable(val messageResource: Int) : Connectivity(messageResource)
        data class TimeOut(val messageResource: Int) : Connectivity(messageResource)
        data class NoConnection(val messageResource: Int) : Connectivity(messageResource)
    }
}
