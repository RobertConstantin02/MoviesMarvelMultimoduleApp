package com.example.retrofit.adapter

import com.example.core.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ResultCallAdapter(
    private val type: Type
): CallAdapter<Type, Call<ApiResponse<Type>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> {
        TODO("Not yet implemented")
    }
}