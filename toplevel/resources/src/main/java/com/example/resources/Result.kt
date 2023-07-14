package com.example.resources

import arrow.core.Either
import retrofit2.HttpException
import java.io.IOException

//sealed class Result<T> {
//    data class Success<T>(val value: T): Result<T>()
//    data class Failure<T>(val exception: Throwable? = null): Result<T>()
//
//    inline fun <R> mapSuccess(transformation: (T) -> R): Result<R> =
//        when(this) {
//            is Success -> Success(transformation(value))
//            is Failure -> Failure(exception)
//        }
//
//    inline fun mapFailure(failure: (Throwable?) -> Throwable?): Result<T> =
//        when(this) {
//            is Success -> Success(value)
//            is Failure -> Failure(failure(exception))
//        }
//
//    inline fun <R> fold(success: (T) -> R, failure: (Throwable?) -> R): R =
//        when(this) {
//            is Success -> success(value)
//            is Failure -> failure(exception)
//        }
//}

interface Error
typealias Result <T> = Either<Error, T>
sealed class RemoteError: Error {
    object Connectivity: RemoteError()
    data class Server(val codeError: Int? = null): RemoteError()
    data class Unknown(val message: String? = null): RemoteError()
}

fun Exception.toError(): RemoteError =
    when(this) {
        is IOException -> RemoteError.Connectivity
        is HttpException -> RemoteError.Server(code())
        else -> RemoteError.Unknown(message.orEmpty())
    }


