package com.example.remote.fake

import com.example.core.implement.IApiErrorHandler
import com.example.core.remote.UnifiedError

class ApiErrorHandlerFake: IApiErrorHandler {
    lateinit var unifiedError: UnifiedError
    override fun invoke(t: Throwable): UnifiedError = unifiedError
}