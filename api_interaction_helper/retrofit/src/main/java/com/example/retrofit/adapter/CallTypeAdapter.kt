package com.example.retrofit.adapter

import com.example.core.ApiResponse
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.facotry.extension.mapToCommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class CallResult<T>(
    proxy: Call<T>,
    private val apiErrorHandler: IApiErrorHandler //inject
): CallWrapper<T, ApiResponse<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResponse<T>>) {
        proxy.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@CallResult,
                            Response.success(ApiResponse.create(response.mapToCommonResponse()))
                        )
                    } else {
                        callback.onFailure(
                            this@CallResult,

                        )
                    }

                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            }
        )
    }

    override fun cloneImpl(): Call<ApiResponse<T>> {
        TODO("Not yet implemented")
    }
}