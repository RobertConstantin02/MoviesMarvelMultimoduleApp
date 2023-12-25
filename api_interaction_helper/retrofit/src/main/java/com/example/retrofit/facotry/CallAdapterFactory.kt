package com.example.retrofit.facotry

import android.util.Log
import com.example.core.remote.ApiResponse
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.adapter.ResultCallAdapter
import retrofit2.Call
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
        Call::class.java -> {
            val callType = getParameterUpperBound(0, returnType as ParameterizedType)
            when (getRawType(callType)) {
                ApiResponse::class.java -> {
                    ResultCallAdapter(
                        getParameterUpperBound(0, callType as ParameterizedType),
                        apiErrorHandler
                    )
                }
                else -> null
            }
        }

        else -> null
    }
}