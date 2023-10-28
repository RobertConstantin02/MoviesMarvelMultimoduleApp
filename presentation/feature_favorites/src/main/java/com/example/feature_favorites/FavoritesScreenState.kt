package com.example.feature_favorites

import com.example.presentation_model.CharacterVo
import com.example.resources.DataBaseError
import com.example.resources.UiText

sealed class FavoritesScreenState {
    object Loading : FavoritesScreenState()
    //object Paging : FavoritesScreenState()

    data class Error(val dataBaseError: DataBaseError) : FavoritesScreenState()
    data class Empty(
        val message: UiText
    ) : FavoritesScreenState()

    data class Success(
        val favoriteCharacters: List<CharacterVo> = emptyList(),
        //val endReached: Boolean = false
    ) : FavoritesScreenState()

}


