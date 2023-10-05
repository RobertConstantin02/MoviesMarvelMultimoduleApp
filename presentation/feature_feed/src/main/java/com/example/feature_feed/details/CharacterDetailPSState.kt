package com.example.feature_feed.details

import com.example.presentation_model.CharacterPresentationScreenVO

sealed class CharacterDetailPSState {
    object Loading : CharacterDetailPSState()
    object Error : CharacterDetailPSState()
    data class Success(val characterDetail: CharacterPresentationScreenVO) : CharacterDetailPSState()
}
