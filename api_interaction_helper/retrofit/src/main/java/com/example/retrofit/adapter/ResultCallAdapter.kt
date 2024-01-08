package com.example.retrofit.adapter

import com.example.core.remote.ApiResponse
import com.example.core.implement.IApiErrorHandler
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ResultCallAdapter(
    private val type: Type,
    private val apiErrorHandler: IApiErrorHandler
): CallAdapter<Type, Call<ApiResponse<Type>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> =
        CallResult(call, apiErrorHandler)
}