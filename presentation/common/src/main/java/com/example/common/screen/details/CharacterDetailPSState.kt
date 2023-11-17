package com.example.common.screen.details

import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.resources.UiText

sealed class CharacterDetailPSState {
    object Loading : CharacterDetailPSState()
    data class Error(val characterDetailError: CharacterDetailError) : CharacterDetailPSState()
    data class Success(val characterDetail: CharacterPresentationScreenVO) : CharacterDetailPSState()
}


sealed class CharacterDetailError(open val message: UiText) {
    data class ServerError(override val message: UiText): CharacterDetailError(message)
    data class ConnectivityError(override val message: UiText): CharacterDetailError(message)
    data class DataBasError(override val message: UiText): CharacterDetailError(message)
    data class UnknownError(override val message: UiText): CharacterDetailError(message)
}