package com.example.resources

import arrow.core.left
import arrow.core.right
import retrofit2.Response

inline fun <R> apiCall(call: () -> Response<R>): Result<R> {
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

