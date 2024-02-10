package com.example.common.screen

import com.example.resources.UiText

sealed class ScreenStateEvent<out T> {
    data class OnSuccess <T>(val data: T): ScreenStateEvent<T>()
    data class OnError<T>(val error: UiText, val data: T?): ScreenStateEvent<T>()
}
