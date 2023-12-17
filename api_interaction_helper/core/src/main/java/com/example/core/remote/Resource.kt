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
        object Loading: State<Nothing>()
        data class Success<out T>(val data: T): State<T>()
        data class SuccessEmpty<out T>(val data: T?): State<T>()

        data class Error<out T>(val apiError: String?, val localError: Int?, val data: T?): State<T>()

        inline fun <R> fold(success: (T) -> R, error: (String?, Int?, T?) -> R, loading: (Unit) -> R, empty: (T) -> R): R =
            when(this) {
                is Error -> error(apiError, localError, data)
                is Loading -> loading(Unit)
                is Success -> TODO()
                is SuccessEmpty -> TODO()
            }

    }


    companion object {
        fun loading() = Resource(State.Loading)

        fun <T> success(data: T) = Resource(State.Success(data))

        fun <T> successEmpty(data: T?) = Resource(State.SuccessEmpty(data)) //review at the end

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