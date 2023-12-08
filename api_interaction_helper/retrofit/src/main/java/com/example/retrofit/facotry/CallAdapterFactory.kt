package com.example.retrofit.facotry

import com.example.core.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class CallAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? = when(getRawType(returnType)) {
        ApiResponse::class.java -> {

        }
        else -> null
    }
}