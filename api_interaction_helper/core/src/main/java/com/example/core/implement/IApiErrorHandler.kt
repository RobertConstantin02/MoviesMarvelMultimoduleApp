package com.example.core.implement

import com.example.core.remote.ApiUnifiedError

interface IApiErrorHandler {
    operator fun invoke(t: Throwable): ApiUnifiedError
}