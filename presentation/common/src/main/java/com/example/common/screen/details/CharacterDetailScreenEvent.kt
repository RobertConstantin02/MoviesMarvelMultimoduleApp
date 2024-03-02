package com.example.common.screen.details

import com.example.common.screen.ScreenStateEvent

sealed class CharacterDetailScreenEvent<T> {
    data class OnScreenState<T>(val screenStateEvent: ScreenStateEvent<T>): CharacterDetailScreenEvent<T>()
    class OnGetCharacterDetails<T>: CharacterDetailScreenEvent<T>()
}