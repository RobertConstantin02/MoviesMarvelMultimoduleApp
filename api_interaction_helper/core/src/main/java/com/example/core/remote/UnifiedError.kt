package com.example.core.remote

sealed class UnifiedError(override val message: String) : Exception() {
    data class Generic(override val message: String) : UnifiedError(message)

    sealed class Http(message: String) : UnifiedError(message) {
        data class Unauthorized(override val message: String) : Http(message = message)
        data class NotFound(override val message: String) : Http(message = message)
        data class InternalError(override val message: String) : Http(message = message)
        data class BadRequest(override val message: String) : Http(message = message)
    }

    sealed class Connectivity(message: String) : UnifiedError(message) {
        data class HostUnreachable(override val message: String) : Connectivity(message)
        data class TimeOut(override val message: String) : Connectivity(message)
        data class NoConnection(override val message: String) : Connectivity(message)
    }
}
