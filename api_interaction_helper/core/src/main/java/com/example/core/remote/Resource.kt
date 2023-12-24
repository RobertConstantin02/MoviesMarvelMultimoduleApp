package com.example.core.remote

/**
 * The factory methods in the companion object (loading, success, emptySuccess, error) provide a
 * convenient and consistent way to create instances of the Resource class. This can improve
 * readability and reduce boilerplate code when creating instances with specific states.
 */
class Resource<out T> private constructor(
    val state: State<T>
) {
    sealed class State<out T> {

        data class Success<out T>(val data: T) : State<T>()
        object SuccessEmpty : State<Nothing>()
        data class Error<out T>(val apiError: String?, val localError: Int?, val data: T?) :
            State<T>()

        fun <R, U> combineResources(
            other: State<R>,
            combineFunction: (T?, R?) -> U
        ): Resource<U> = when (this) {
            is Success -> when (other) {
                is Success -> Resource(Success(combineFunction(data, other.data)))
                is SuccessEmpty -> Resource(SuccessEmpty)
                is Error -> Resource(Error(other.apiError, other.localError, combineFunction(data, other.data)))
            }
            is SuccessEmpty -> Resource(SuccessEmpty)
            is Error -> Resource(Error(apiError, localError, combineFunction(data, (other as? Error)?.data)))
        }

        fun unwrap(): T? {
            return when (this) {
                is Success -> this.data
                is SuccessEmpty -> null
                is Error -> this.data
            }
        }

        fun <R> mapSuccess(success: (data: T?) -> R): Resource<R> {
            return when(this) {
                is Success -> Resource(Success(success(this.data)))
                is Error -> Resource(Error(this.apiError, this.localError, success(this.data)))
                is SuccessEmpty -> Resource(SuccessEmpty)
            }
        }

        inline fun <R> fold(
            success: (data: T) -> R,
            error: (e: Error<T>) -> R,
            empty: (SuccessEmpty) -> R
        ): R {
            return when(this) {
                is Success -> success(this.data)
                is Error -> error(this)
                is SuccessEmpty -> empty(SuccessEmpty)
            }
        }
    }


    companion object {
        fun <T> success(data: T) = Resource(State.Success(data))

        fun successEmpty() = Resource(State.SuccessEmpty) //review at the end

        fun <T> error(
            errorMessage: String,
            data: T?
        ) = Resource(State.Error(errorMessage, null, data))

        fun <T> error(
            errorResource: Int,
            data: T?
        ) = Resource(State.Error(null, errorResource, data))
    }
}