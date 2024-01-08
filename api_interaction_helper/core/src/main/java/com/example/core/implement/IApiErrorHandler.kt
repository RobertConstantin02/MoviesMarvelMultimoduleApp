package com.example.core.implement

import com.example.core.remote.UnifiedError

interface IApiErrorHandler {
    operator fun invoke(t: Throwable): UnifiedError
}