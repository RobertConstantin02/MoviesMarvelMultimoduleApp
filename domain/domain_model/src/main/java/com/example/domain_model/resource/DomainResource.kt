package com.example.domain_model.resource

import com.example.domain_model.error.DomainUnifiedError

class DomainResource<out T> private constructor(
    val domainState: DomainState<T>
) {
    sealed class DomainState<out T> {

        data class Success<out T>(val data: T) : DomainState<T>()
        object SuccessEmpty : DomainState<Nothing>()
        data class Error<out T>(val error: DomainUnifiedError, val data: T?) :
            DomainState<T>()

        fun <R, U> combineResources(
            other: DomainState<R>,
            combineFunction: (T?, R?) -> U
        ): DomainResource<U> = when (this) {
            is Success -> when (other) {
                is Success -> {
                    DomainResource(Success(combineFunction(data, other.data)))
                }

                is SuccessEmpty -> DomainResource(SuccessEmpty)
                is Error -> DomainResource(Error(other.error, combineFunction(data, other.data)))
            }

            is SuccessEmpty -> DomainResource(SuccessEmpty)
            is Error -> DomainResource(Error(error, combineFunction(data, (other as? Error)?.data)))
        }

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

        fun unwrap(): T? {
            return when (this) {
                is Success -> this.data
                is SuccessEmpty -> null
                is Error -> this.data
            }
        }
    }

    companion object {
        fun <T> success(data: T) = DomainResource(DomainState.Success(data))

        fun successEmpty() = DomainResource(DomainState.SuccessEmpty)

        fun <T> error(
            apiError: DomainUnifiedError,
            data: T?,
        ) = DomainResource(DomainState.Error(apiError, data))

        fun error(
            localError: DomainUnifiedError
        ) = DomainResource(DomainState.Error(localError, null))
    }
}