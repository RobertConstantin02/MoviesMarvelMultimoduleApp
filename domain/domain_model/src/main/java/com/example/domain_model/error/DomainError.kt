package com.example.domain_model.error

//sealed class DomainError<T> {
//    data class ApiError<T>(val message: String?, val code: Int?, val data: T?): DomainError<T>()
//    data class LocalError<T>(val error: Int): DomainError<T>()
//
//}

interface DomainUnifiedError

sealed class DomainApiUnifiedError(open val message: String?, open val code: Int?): DomainUnifiedError {
    data class Generic(override val message: String?) : DomainApiUnifiedError(message, null)

    sealed class Http(message: String, errorCode: Int) : DomainApiUnifiedError(message, errorCode) {
        data class Unauthorized(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class NotFound(override val message: String, val errorCode: Int) : Http( message, errorCode)
        data class InternalErrorApi(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class BadRequest(override val message: String, val errorCode: Int) : Http(message, errorCode)
        data class EmptyResponse(override val message: String, val errorCode: Int) : Http(message, errorCode)
    }

    sealed class Connectivity(message: String?) : DomainApiUnifiedError(message, null) {
        data class HostUnreachable(override val message: String?) : Connectivity(message)
        data class TimeOut(override val message: String?) : Connectivity(message)
        data class NoConnection(override val message: String?) : Connectivity(message)
    }
}

sealed class DomainLocalUnifiedError: DomainUnifiedError {
    object Insertion: DomainLocalUnifiedError()
    object Deletion: DomainLocalUnifiedError()
    object Update: DomainLocalUnifiedError()
    object Reading: DomainLocalUnifiedError()

}

data class CoroutineError(val message: String?): DomainUnifiedError
