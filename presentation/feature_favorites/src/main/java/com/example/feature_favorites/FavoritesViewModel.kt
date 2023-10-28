package com.example.feature_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.R
import com.example.feature_favorites.paginator.PaginationState
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

    private val _paginationState = MutableStateFlow<PaginationState>(PaginationState.Idle)
    val paginationState = _paginationState.asStateFlow()

    private var currentPage = -1
    private var currentCharacterList = mutableListOf<CharacterVo>()
    var canPaginate by mutableStateOf(true)

    private val pagination = Paginator(
        initialKey = currentPage,
        onLoading = {
            if (currentPage == -1) _favoritesState.update { FavoritesScreenState.Loading }
            else _paginationState.update { PaginationState.Loading }
        },
        onRequest = { nextPage ->
            getFavoriteCharacters.invoke(FavoritesParams(currentPage), Dispatchers.IO)
        },
        getNextKey = { currentPage++ },
        onSuccess = { newCharacters ->
            canPaginate = newCharacters.size == 10
            if(!canPaginate) _paginationState.update { PaginationState.PaginationEnd }
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

    fun loadNextCharacters() { viewModelScope.launch {
        pagination.loadNextData() }
    }

    private fun simulateLoading() {
        viewModelScope.launch {
            delay(LOADING_SIMULATION)
            _paginationState.update { PaginationState.Idle }
        }
    }
}