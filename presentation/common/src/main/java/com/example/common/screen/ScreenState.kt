package com.example.common.screen

import com.example.resources.UiText

sealed class ScreenState<T> {
    class Loading<T> : ScreenState<T>()
    data class Error<T>(val message: UiText, val data: T?) : ScreenState<T>()
    data class Success<T>(val data: T) : ScreenState<T>()
    data class Empty<T>(val message: UiText) : ScreenState<T>()
}