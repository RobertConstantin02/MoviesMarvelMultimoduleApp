package com.example.core.implement

import com.example.core.UnifiedError

interface IApiErrorHandler {
    operator fun invoke(t: Throwable): UnifiedError
}