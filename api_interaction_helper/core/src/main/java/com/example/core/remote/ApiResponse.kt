package com.example.core.remote

import com.example.core.implement.Response
import java.net.HttpURLConnection.HTTP_BAD_REQUEST

sealed class ApiResponse<T> {

    companion object {
        fun <T> create(unifiedError: UnifiedError): ApiResponseError<T> =
            ApiResponseError(unifiedError)

        fun <T> create(response: Response<T>, httpException: () -> ApiResponse<T>): ApiResponse<T> =
            if (response.isSuccessful) {
                with(response.body()){
                    if (this == null || response.code == HTTP_BAD_REQUEST) ApiResponseEmpty()
                    else ApiResponseSuccess(this)
                }
            } else httpException()
    }
}

data class ApiResponseSuccess<T>(val body: T) : ApiResponse<T>()
class ApiResponseEmpty<T> : ApiResponse<T>()
data class ApiResponseError<T>(val unifiedError: UnifiedError) : ApiResponse<T>()


