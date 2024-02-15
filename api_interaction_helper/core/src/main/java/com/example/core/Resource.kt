package com.example.core

import com.example.core.local.LocalUnifiedError
import com.example.core.remote.ApiUnifiedError

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
        data class Error<out T>(val error: UnifiedError, val data: T?) :
            State<T>()

        inline fun <R> fold(
            success: (data: T) -> R,
            error: (e: Error<T>) -> R,
            empty: (SuccessEmpty) -> R
        ): R {
            return when (this) {
                is Success -> success(this.data)
                is Error -> error(this)
                is SuccessEmpty -> empty(SuccessEmpty)
            }
        }
    }


    companion object {
        fun <T> success(data: T) = Resource(State.Success(data))

        fun successEmpty() = Resource(State.SuccessEmpty)

        fun <T> error(
            apiError: ApiUnifiedError,
            data: T?,
        ) = Resource(State.Error(apiError, data))

        fun error(
            localError: LocalUnifiedError
        ) = Resource(State.Error(localError, null))
    }
}