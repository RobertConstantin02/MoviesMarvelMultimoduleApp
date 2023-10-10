package com.example.feature_feed.details

import com.example.presentation_model.CharacterPresentationScreenVO

sealed class CharacterDetailPSEvent {
    data class Found(val characterPresentationScreen: CharacterPresentationScreenVO): CharacterDetailPSEvent()
    //object NotFound: CharacterDetailPSEvent() ????
    data class Error(val error: CharacterDetailPSState): CharacterDetailPSEvent()
}
