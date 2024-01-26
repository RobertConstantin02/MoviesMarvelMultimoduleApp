package com.example.retrofit.adapter

import com.example.core.remote.ApiResponse
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.facotry.extension.mapToCommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

/**
 * Custom implementation of the Retrofit Call interface, designed to handle API error responses in a
 * centralized way.
 *
 * Inside the overridden enqueue method, a new callback is provided to the original call.enqueue method.
 * In the overridden callback's onResponse method:
 *      If the response is successful (response.isSuccessful), the original callback is invoked with
 *      the successful response.
 *      If the response is not successful, an HttpException is created from the response, and the
 *      error is handled by invoking the apiErrorHandlingUseCase with the HttpException.
 * In the overridden callback's onFailure method:
 *      If there is a failure in making the network call, the error is handled by invoking the
 *      apiErrorHandlingUseCase with the throwable.
 *
 * @onResponse: is invoked when a successful response is received from the server.
 *     -> @response: parameter contains information about the successful response
 * @onFailure: is invoked when there is a failure during the network request.
 *     -> @t : contains information about the failure
 *
 * @apiErrorHandler: handles errors depending on whether it is IOException or HttpException
 */
internal class CallResult<T>(
    proxy: Call<T>,
    private val apiErrorHandler: IApiErrorHandler
) : CallWrapper<T, ApiResponse<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResponse<T>>) {
        proxy.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callback.onResponse(this@CallResult, response.handleOnResponse())
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onResponse(
                        this@CallResult,
                        Response.success(ApiResponse.create(apiErrorHandler.invoke(t)))
                    )
                }
            }
        )
    }

    override fun cloneImpl(): Call<ApiResponse<T>> {
        TODO("Not yet implemented")
    }

    private fun<T> Response<T>.handleOnResponse() = Response.success(
        ApiResponse.create(
            this.mapToCommonResponse(),
            httpException = {
                ApiResponse.create(apiErrorHandler.invoke(HttpException(this)))
            }
        )
    )
}