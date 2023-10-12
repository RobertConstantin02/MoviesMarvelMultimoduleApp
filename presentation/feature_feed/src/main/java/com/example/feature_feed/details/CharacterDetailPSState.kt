package com.example.feature_feed.details

import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.resources.UiText

sealed class CharacterDetailPSState {
    object Loading : CharacterDetailPSState()
    data class Error(val characterDetailPSError: CharacterDetailPSError) : CharacterDetailPSState()
    data class Success(val characterDetail: CharacterPresentationScreenVO) : CharacterDetailPSState()
}


sealed class CharacterDetailPSError(open val message: UiText) {
    data class ServerError(override val message: UiText): CharacterDetailPSError(message)
    data class ConnectivityError(override val message: UiText): CharacterDetailPSError(message)
    data class DataBasError(override val message: UiText): CharacterDetailPSError(message)
    data class UnknownError(override val message: UiText): CharacterDetailPSError(message)
}