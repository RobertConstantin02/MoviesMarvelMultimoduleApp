package com.example.remote.fake

import com.example.core.implement.IApiErrorHandler
import com.example.core.remote.ApiUnifiedError

class ApiErrorHandlerFake: IApiErrorHandler {
    lateinit var apiUnifiedError: ApiUnifiedError
    override fun invoke(t: Throwable): ApiUnifiedError = apiUnifiedError
}