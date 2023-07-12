package com.example.resources

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    data class Failure<T>(val exception: Throwable? = null): Result<T>()

    inline fun <R> mapSuccess(transformation: (T) -> R): Result<R> =
        when(this) {
            is Success -> Success(transformation(value))
            is Failure -> Failure(exception)
        }

    inline fun mapFailure(failure: (Throwable?) -> Throwable?): Result<T> =
        when(this) {
            is Success -> Success(value)
            is Failure -> Failure(failure(exception))
        }

    inline fun <R> fold(success: (T) -> R, failure: (Throwable?) -> R): R =
        when(this) {
            is Success -> success(value)
            is Failure -> failure(exception)
        }
}

