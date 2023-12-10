package com.example.retrofit.adapter

import com.example.core.remote.ApiResponse
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.facotry.extension.mapToCommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

// TODO: I need at same time passing proxy and the handler which can be injected but proxy no.
//If not I have to pass it in the factory....

//Here we can use @AssistedInjection
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
            httpException = { ApiResponse.create(apiErrorHandler.invoke(HttpException(this))) }
        )
    )
}