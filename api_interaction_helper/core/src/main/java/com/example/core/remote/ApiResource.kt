package com.example.core.remote

import com.example.core.Resource

suspend inline fun <REMOTE> networkResource(
    crossinline makeNetworkRequest: suspend () -> ApiResponse<REMOTE>,
    crossinline onNetworkRequestFailed: (apiUnifiedError: ApiUnifiedError) -> Unit = { _: ApiUnifiedError -> },
): Resource<REMOTE> {

    return when (val apiResponse = makeNetworkRequest()) {
        is ApiResponseSuccess -> Resource.success(data = apiResponse.body)
        is ApiResponseError -> {
            onNetworkRequestFailed(apiResponse.apiUnifiedError)
            Resource.error(apiResponse.apiUnifiedError, null)
        }
        is ApiResponseEmpty -> Resource.successEmpty()
    }
}