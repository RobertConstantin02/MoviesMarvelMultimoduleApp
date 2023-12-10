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
        object SuccessEmpty: State<Nothing>()

        data class Error(val errorMessage: String, ): State<Nothing>()
    }

    companion object {
        fun loading() = Resource(State.Loading)

        fun <T> success(data: T) = Resource(State.Success(data))

        fun successEmpty() = Resource(State.SuccessEmpty)

        fun error(errorMessage: String) = Resource(State.Error(errorMessage))
    }
}