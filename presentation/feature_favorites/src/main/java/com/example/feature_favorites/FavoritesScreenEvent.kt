package com.example.feature_favorites

import com.example.presentation_model.CharacterVo
import com.example.resources.UiText

sealed class FavoritesScreenEvent {
    object OnLoadData: FavoritesScreenEvent()
    data class OnListFound(val newCharacters: List<CharacterVo>): FavoritesScreenEvent()
    data class OnListEmpty(val messageInfo: UiText): FavoritesScreenEvent()
    data class OnError(val errorMessage: UiText): FavoritesScreenEvent()
    object OnCancellCollectData: FavoritesScreenEvent()
}
