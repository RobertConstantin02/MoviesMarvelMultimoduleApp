package com.example.resources

import arrow.core.Either
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

//sealed interface makes that when it comes to check for errors remote and dataBase errors, only those two types
//can be. So taht in onError function from Details then we wont have
sealed interface DataSourceError

typealias Result <T> = Either<Throwable, T>
sealed class RemoteError: Throwable(), DataSourceError {
    object Connectivity: RemoteError()
    data class Server(val codeError: Int? = null): RemoteError()
    data class Unknown(override val message: String? = null): RemoteError()
}

sealed class DataBaseError: Exception(), DataSourceError {
    object EmptyResult: DataBaseError()
    object ItemNotFound: DataBaseError()
    object InsertionError: DataBaseError()
    object DeletionError: DataBaseError()
    object UpdateError: DataBaseError()
}


fun Exception.toError(): RemoteError =
    when(this) {
        is IOException -> RemoteError.Connectivity
        is HttpException -> RemoteError.Server(code())
        else -> RemoteError.Unknown(message.orEmpty())
    }

//fun <T> Flow<T>.asResult(): Flow<Result<T>> {
//    return this
//        .map<T, Result<T>> {
//            it.right()
//        }
//        .onStart { emit(T) }
//        .catch { emit(T) }
//}



//fun Throwable.toErrorType() = when (this) {
//    is IOException -> ErrorType.Api.Network
//    is HttpException -> when (code()) {
//        ErrorCodes.Http.ResourceNotFound -> ErrorType.Api.NotFound
//        ErrorCodes.Http.InternalServer -> ErrorType.Api.Server
//        ErrorCodes.Http.ServiceUnavailable -> ErrorType.Api.ServiceUnavailable
//        else -> ErrorType.Unknown
//    }
//    else -> ErrorType.Unknown
//}
