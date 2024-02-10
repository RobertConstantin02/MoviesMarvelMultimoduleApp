package com.example.common.screen.details

import com.example.common.screen.ScreenStateEvent

sealed class CharacterDetailPSEvent<T> {
    data class OnScreenState<T>(val screenStateEvent: ScreenStateEvent<T>): CharacterDetailPSEvent<T>()
    class OnGetCharacterDetails<T>: CharacterDetailPSEvent<T>()
}