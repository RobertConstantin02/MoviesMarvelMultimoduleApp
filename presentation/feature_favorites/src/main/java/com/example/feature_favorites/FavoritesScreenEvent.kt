package com.example.feature_favorites

import com.example.common.screen.ScreenStateEvent
import com.example.resources.UiText

sealed class FavoritesScreenEvent<T> {
    class OnLoadData<T>: FavoritesScreenEvent<T>()
    data class OnScreenState<T>(val screenStateEvent: ScreenStateEvent<T>): FavoritesScreenEvent<T>()
    data class OnRemoveFavorite<T>(val isFavorite: Boolean = false, val characterId: Int): FavoritesScreenEvent<T>()
    class OnCancelCollectData<T>: FavoritesScreenEvent<T>()
}
