package com.example.retrofit.facotry

import com.example.core.ApiResponse
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.adapter.ResultCallAdapter
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class CallAdapterFactory @Inject constructor(
    private val apiErrorHandler: IApiErrorHandler
) : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? = when (getRawType(returnType)) {
        ApiResponse::class.java ->
            ResultCallAdapter(
                getParameterUpperBound(0, returnType as ParameterizedType),
                apiErrorHandler
            )

        else -> null
    }
}