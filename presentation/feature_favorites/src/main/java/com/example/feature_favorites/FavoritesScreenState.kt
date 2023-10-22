package com.example.feature_favorites

import androidx.paging.PagingData
import com.example.presentation_model.CharacterVo
import com.example.resources.DataBaseError
import com.example.resources.UiText
import kotlinx.coroutines.flow.Flow

sealed class FavoritesScreenState {
    object Loading: FavoritesScreenState()
    data class Error(val dataBaseError: DataBaseError) : FavoritesScreenState()
    data class Empty(
        val message: UiText
        ) : FavoritesScreenState()
    data class Success(
        val favoriteCharacters: List<CharacterVo> = emptyList(),
        //val endReached: Boolean = false
    ) : FavoritesScreenState()
}
