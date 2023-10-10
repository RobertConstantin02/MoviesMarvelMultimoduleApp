package com.example.feature_feed.details

import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.resources.UiText

sealed class CharacterDetailPSState {
    object Loading : CharacterDetailPSState()
    data class Error(val characterDetailPSError: CharacterDetailPSError) : CharacterDetailPSState()
    data class Success(val characterDetail: CharacterPresentationScreenVO) : CharacterDetailPSState()
}


sealed class CharacterDetailPSError {
    data class ServerError(val message: UiText): CharacterDetailPSError()
    data class ConnectivityError(val message: UiText): CharacterDetailPSError()
    data class DataBasError(val message: UiText): CharacterDetailPSError()
    data class UnknownError(val message: UiText): CharacterDetailPSError()
}