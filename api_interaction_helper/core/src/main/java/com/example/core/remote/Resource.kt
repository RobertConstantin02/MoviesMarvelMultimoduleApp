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

        data class Error<out T>(val apiError: String?, val localError: Int?, val data: T?) :
            State<T>()

//        fun <R1, R2, W> Resource.State<R1>.combineResource(
//            resource: Resource.State<R2>,
//            transform: (a: R1, b: R2) -> W,
//            //currentState: State<T>,
//            //unSuccess: (currentState : State<T>) -> Resource()
//        ): Resource<W> =
//            when {
//                this is Resource.State.Success && resource is State.Success -> Resource(
//                    State.Success(transform(this.data, resource.data))
//                )
//                this is Resource.State.Error -> Resource(State.Error(this.apiError, this.localError, this.data))
//                else -> Resource(Loading)
//            }

        fun <R1, R2, W> Resource.State<R1>.combineSuccess(
            resource: Resource.State<R2>,
            transform: (a: R1, b: R2) -> W,
        ): Resource<W>? = let {
            if (this is Resource.State.Success && resource is State.Success)
                Resource(State.Success(transform(this.data, resource.data)))
            else null
        }

        fun <R1> Resource<R1>.unWrap(
        ): R1? = let {
            if (this.state is Success)
                this.state.data
            else null
        }






//        fun <T, R> Resource<T>.mapData(transform: (T?) -> R): Resource<R> {
//            return when (val currentState = state) {
//                is Resource.State.Loading -> Resource(transform())
//                is Resource.State.Success -> Resource(Resource.State.Success(transform(currentState.data)))
//                is Resource.State.SuccessEmpty -> Resource(
//                    Resource.State.SuccessEmpty(
//                        transform(
//                            currentState.data
//                        )
//                    )
//                )
//
//                is Resource.State.Error -> Resource(
//                    Resource.State.Error(
//                        currentState.apiError,
//                        currentState.localError,
//                        transform(currentState.data)
//                    )
//                )
//            }
//        }

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