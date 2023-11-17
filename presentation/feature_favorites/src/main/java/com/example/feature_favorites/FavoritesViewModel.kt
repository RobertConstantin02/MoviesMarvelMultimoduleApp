package com.example.feature_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.R
import com.example.feature_favorites.paginator.Paginator
import com.example.presentation_mapper.toCharacterVo
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText
import com.example.usecase.character.FavoritesParams
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.character.UpdateParams
import com.example.usecase.di.GetFavoriteCharacters
import com.example.usecase.di.UpdateCharacterIsFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LOADING_SIMULATION = 500L

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @GetFavoriteCharacters private val getFavoriteCharacters: IGetFavoriteCharactersUseCase,
    @UpdateCharacterIsFavorite private val updateCharacterIsFavorite: IUpdateCharacterIsFavoriteUseCase
) : ViewModel() {

    private val _favoritesState =
        MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val favoritesState = _favoritesState.asStateFlow()

    private val _paginationState = MutableStateFlow<Paginator.State>(Paginator.State.Idle)
    val paginationState = _paginationState.asStateFlow()

    private var currentPage = PAGE_INITIALIZATION
    private var currentCharacterList = mutableListOf<CharacterVo>()
    var canPaginate by mutableStateOf(true)
        private set

    private val pagination = Paginator(
        initialKey = currentPage,
        onLoading = {
            if (currentPage == -1) _favoritesState.update { FavoritesScreenState.Loading }
            else _paginationState.update { Paginator.State.Loading }
        },
        onRequest = { nextPage ->
            getFavoriteCharacters.invoke(FavoritesParams(nextPage), Dispatchers.IO)
        },
        getNextKey = { currentPage++ },
        onSuccess = { newCharacters ->
            canPaginate = newCharacters.size == PAGE_SIZE
            if (!canPaginate && itemListHasPageSizeOrGrater()) _paginationState.update { Paginator.State.End }
            onSuccess(newCharacters.map { it.toCharacterVo() })
        },
        onError = { error -> }
    )

    fun updateCharacter(isFavorite: Boolean, characterId: Int) {
        pagination.stopCollection()
        updateCharacterIsFavorite.invoke(
            UpdateParams(isFavorite, characterId),
            viewModelScope,
            Dispatchers.IO
        )
        currentCharacterList.remove(currentCharacterList.find { it.id == characterId })
        _favoritesState.update { FavoritesScreenState.Success(currentCharacterList.toList()) }
    }

    private fun onSuccess(newCharacters: List<CharacterVo>) {
        if ((currentCharacterList + newCharacters).isEmpty()) {
            _favoritesState.update {
                FavoritesScreenState.Empty(UiText.StringResources(R.string.empty_favorite_list))
            }
        } else {
            simulateLoading()
            _favoritesState.update {
                FavoritesScreenState.Success(currentCharacterList + newCharacters)
            }
            currentCharacterList.addAll(newCharacters)
        }
    }

    fun loadNextCharacters() {
        viewModelScope.launch { pagination.loadNextData() }
    }

    private fun itemListHasPageSizeOrGrater() =
        ((_favoritesState.value as? FavoritesScreenState.Success)?.favoriteCharacters?.size
            ?: 0) >= PAGE_SIZE

    private fun simulateLoading() {
        viewModelScope.launch {
            delay(LOADING_SIMULATION)
            _paginationState.update { Paginator.State.Idle }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val PAGE_INITIALIZATION = -1
    }
}