package com.example.core

import com.example.core.implement.Response

const val UNKNOWN_ERROR = "Unknown Error"
sealed class ApiResponse<T> {

    companion object {
        fun <T> create(unifiedError: UnifiedError): ApiResponseError<T> =
            ApiResponseError(unifiedError)


        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                with(response.body()) {
                    if (this == null || response.code == 204) ApiResponseEmpty()
                    else ApiResponseSuccess(this)
                }
            } else {
//                ApiResponseError(
//                    errorMessage = response.errorDescription,
//                    httpStatusCode = response.code
//                )
            }
        }
    }
}

data class ApiResponseSuccess<T>(val body: T?) : ApiResponse<T>()
class ApiResponseEmpty<T> : ApiResponse<T>()

// TODO: here we can change it for unifiedError
data class ApiResponseError<T>(
//    val errorMessage: String,
//    val httpStatusCode: Int
    val unifiedError: UnifiedError
) : ApiResponse<T>()


