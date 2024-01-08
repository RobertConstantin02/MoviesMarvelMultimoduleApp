package com.example.feature_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.R
import com.example.domain_model.error.DomainError
import com.example.feature_favorites.paginator.Paginator
import com.example.presentation_mapper.toCharacterVo
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.di.GetFavoriteCharacters
import com.example.usecase.di.UpdateCharacterIsFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private var job: Job? = null
    var canPaginate by mutableStateOf(true)
        private set

    private val pagination = Paginator(
        initialKey = currentPage,
        onLoading = {
            if (currentPage == -1) _favoritesState.update { FavoritesScreenState.Loading }
            else _paginationState.update { Paginator.State.Loading }
        },
        onRequest = { nextPage ->
            getFavoriteCharacters.invoke(IGetFavoriteCharactersUseCase.Params(nextPage), Dispatchers.IO)
        },
        getNextKey = {
            currentPage += 1
            currentPage
        },
        onSuccess = { newCharacters ->
            canPaginate = newCharacters.size == PAGE_SIZE
            if (!canPaginate && itemListHasPageSizeOrGrater()) _paginationState.update { Paginator.State.End }
            onSuccess(newCharacters.map { it.toCharacterVo() })
        },
        onError = ::onError,
        onEmpty = { onEvent(FavoritesScreenEvent.OnListEmpty(UiText.StringResources(R.string.empty_favorite_list))) }
    )


    fun onEvent(event: FavoritesScreenEvent) {
        when (event) {
            is FavoritesScreenEvent.OnLoadData -> {
                job?.cancel()
                job = viewModelScope.launch { pagination.loadNextData() }
            }

            is FavoritesScreenEvent.OnListFound ->
                _favoritesState.update { FavoritesScreenState.Success(event.newCharacters) }

            is FavoritesScreenEvent.OnListEmpty ->
                _favoritesState.update {
                    FavoritesScreenState.Empty(UiText.StringResources(R.string.empty_favorite_list))
                }

            is FavoritesScreenEvent.OnError ->
                _favoritesState.update { FavoritesScreenState.Error(event.errorMessage) }

            is FavoritesScreenEvent.OnCancellCollectData -> {
                job?.cancel()
            }
        }
    }

    private fun onError(localError: Int) {
        onEvent(FavoritesScreenEvent.OnError(UiText.StringResources(localError)))
    }

    private fun onSuccess(newCharacters: List<CharacterVo> = emptyList()) {
        if ((currentCharacterList + newCharacters).isNotEmpty()) {
            simulateLoading()
            onEvent(FavoritesScreenEvent.OnListFound(currentCharacterList + newCharacters))
            currentCharacterList.addAll(newCharacters)
        } else onEvent(FavoritesScreenEvent.OnListEmpty(UiText.StringResources(R.string.empty_favorite_list)))
    }

    fun updateCharacter(isFavorite: Boolean, characterId: Int) {
        pagination.stopCollection() //for not duplicate data when removing
        updateCharacterIsFavorite.invoke(
            IUpdateCharacterIsFavoriteUseCase.Params(isFavorite, characterId),
            Dispatchers.IO,
            viewModelScope,
            success = {
                currentCharacterList.remove(currentCharacterList.find { it.id == characterId })
                onSuccess()
            },
            error = { error ->
                (error as? DomainError.LocalError)?.let {
                    onEvent(FavoritesScreenEvent.OnError(UiText.StringResources(it.error)))
                }
            }
        )
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