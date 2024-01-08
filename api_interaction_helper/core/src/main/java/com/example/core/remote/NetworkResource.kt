package com.example.core.remote

import com.example.core.Resource

suspend inline fun <REMOTE> networkResource(
    crossinline makeNetworkRequest: suspend () -> ApiResponse<REMOTE>,
    crossinline onNetworkRequestFailed: (unifiedError: UnifiedError) -> Unit = { _: UnifiedError -> },
): Resource<REMOTE> {

    return when (val apiResponse = makeNetworkRequest()) {
        is ApiResponseSuccess -> Resource.success(data = apiResponse.body)
        is ApiResponseError -> {
            onNetworkRequestFailed(apiResponse.unifiedError)
            Resource.error(apiResponse.unifiedError.message, null)
        }

        is ApiResponseEmpty -> Resource.successEmpty()
    }
}