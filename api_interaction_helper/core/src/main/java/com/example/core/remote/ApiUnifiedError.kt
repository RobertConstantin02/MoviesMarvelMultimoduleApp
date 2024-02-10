package com.example.core.remote

import com.example.core.UnifiedError

sealed class ApiUnifiedError(open val message: String?, open val code: Int?): UnifiedError {
    data class Generic(override val message: String?) : ApiUnifiedError(message, null)

    sealed class Http(message: String, errorCode: Int) : ApiUnifiedError(message, errorCode) {
        data class Unauthorized(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class NotFound(override val message: String, val errorCode: Int) : Http( message, errorCode)
        data class InternalErrorApi(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class BadRequest(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class EmptyResponse(override val message: String, val errorCode: Int) : Http(message, errorCode)
    }

    sealed class Connectivity(message: String?) : ApiUnifiedError(message, null) {
        data class HostUnreachable(override val message: String?) : Connectivity(message)
        data class TimeOut(override val message: String?) : Connectivity(message)
        data class NoConnection(override val message: String?) : Connectivity(message)
    }
}
