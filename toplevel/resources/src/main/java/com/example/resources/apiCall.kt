package com.example.resources

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


interface DataError
typealias DataResult <T> = Either<DataError, T>

sealed class RemoteError: DataError {
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


inline fun <R> apiCall(call: () -> Response<R>): DataResult<R> {
    return try {
        with(call.invoke()) {
            when {
                isSuccessful && body() != null -> body()!!.right()
                else -> RemoteError.Server(this.code()).left()
            }
        }
    } catch (exception: Exception) {
        exception.toError().left()
    }
}

