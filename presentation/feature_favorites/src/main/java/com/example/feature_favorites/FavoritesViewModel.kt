package com.example.feature_favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.R
import com.example.common.paginatorFactory.PaginationFactory
import com.example.common.screen.ScreenState
import com.example.common.screen.ScreenStateEvent
import com.example.common.util.translateError
import com.example.domain_model.error.DomainUnifiedError
import com.example.feature_favorites.paginator.FavoritePagination
import com.example.feature_favorites.paginator.FavoritePagingConfig
import com.example.presentation_mapper.toCharacterVo
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.di.GetFavoriteCharacters
import com.example.usecase.di.UpdateCharacterIsFavorite
import dagger.hilt.android.lifecycle.HiltViewModel
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
    @UpdateCharacterIsFavorite private val updateCharacterIsFavorite: IUpdateCharacterIsFavoriteUseCase,
    paginationFactory: PaginationFactory<FavoritePagingConfig>
) : ViewModel() {

    private val _favoritesState =
        MutableStateFlow<ScreenState<List<CharacterVo>>>(ScreenState.Loading())
    val favoritesState = _favoritesState.asStateFlow()

    private val _paginationState = MutableStateFlow<FavoritePagination.State>(FavoritePagination.State.Idle)
    val paginationState = _paginationState.asStateFlow()

    private var currentPage = PAGE_INITIALIZATION
    private var currentCharacterList = mutableListOf<CharacterVo>()

    private var job: Job? = null
    var canPaginate by mutableStateOf(true)
        private set

    private val pagination = paginationFactory.createPagination(
        configuration = FavoritePagingConfig(
            initialKey = currentPage,
            onLoading = {
                if (currentPage != -1) _paginationState.update { FavoritePagination.State.Loading }
            },
            onRequest = { nextPage ->
                getFavoriteCharacters.invoke(IGetFavoriteCharactersUseCase.Params(nextPage))
            },
            getNextKey = {
                currentPage += 1
                currentPage
            },
            onSuccess = { newCharacters ->
                canPaginate = newCharacters.size == PAGE_SIZE
                if (!canPaginate && itemListHasPageSizeOrGrater()) _paginationState.update { FavoritePagination.State.End }
                onSuccess(newCharacters.map { it.toCharacterVo() })
            },
            onError = ::onError,
            onEmpty = { onEvent(FavoritesScreenEvent.OnScreenState(ScreenStateEvent.OnEmpty(UiText.StringResources(R.string.empty_favorite_list))))  }
        )
    )


    fun onEvent(event: FavoritesScreenEvent<List<CharacterVo>>) {
        when (event) {
            is FavoritesScreenEvent.OnLoadData -> {
                job?.cancel()
                job = viewModelScope.launch { pagination.loadNextData() }
            }

            is FavoritesScreenEvent.OnScreenState -> {
                when (event.screenStateEvent) {
                    is ScreenStateEvent.OnError -> {
                        _favoritesState.update {
                            ScreenState.Error(event.screenStateEvent.error, null)
                        }
                    }

                    is ScreenStateEvent.OnSuccess -> {
                        _favoritesState.update {
                            ScreenState.Success(event.screenStateEvent.data)
                        }
                    }

                    is ScreenStateEvent.OnEmpty -> {
                        _favoritesState.update {
                            ScreenState.Empty(UiText.StringResources(R.string.empty_favorite_list))
                        }
                    }
                }
            }

            is FavoritesScreenEvent.OnRemoveFavorite ->
                removeFavoriteCharacter(event.isFavorite, event.characterId)

            is FavoritesScreenEvent.OnCancelCollectData -> {
                job?.cancel()
            }
        }
    }

    private fun onError(localError: DomainUnifiedError) {
        onEvent(FavoritesScreenEvent.OnScreenState(translateError(localError, null)))
    }

    private fun onSuccess(newCharacters: List<CharacterVo> = emptyList()) {
        if ((currentCharacterList + newCharacters).isNotEmpty()) {
            simulateLoading()
            onEvent(
                FavoritesScreenEvent.OnScreenState(
                    ScreenStateEvent.OnSuccess(
                        currentCharacterList + newCharacters
                    )
                )
            )
            currentCharacterList.addAll(newCharacters)
        } else onEvent(FavoritesScreenEvent.OnScreenState(ScreenStateEvent.OnEmpty(UiText.StringResources(R.string.empty_favorite_list))))
    }

    private fun onSuccess() {
        if (currentCharacterList.isNotEmpty()) {
            onEvent(
                FavoritesScreenEvent.OnScreenState(ScreenStateEvent.OnSuccess(currentCharacterList))
            )
        } else onEvent(FavoritesScreenEvent.OnScreenState(ScreenStateEvent.OnEmpty(UiText.StringResources(R.string.empty_favorite_list))))
    }

    private fun removeFavoriteCharacter(isFavorite: Boolean, characterId: Int) {
        pagination.stopCollection() //for not duplicate data when removing
        updateCharacterIsFavorite.invoke(
            IUpdateCharacterIsFavoriteUseCase.Params(isFavorite, characterId),
            viewModelScope,
            success = {
                currentCharacterList.remove(currentCharacterList.find { it.id == characterId })
                onSuccess()
            },
            error = { localError, _ ->
                onEvent(FavoritesScreenEvent.OnScreenState(translateError(localError, null)))
            }
        )
    }

    private fun itemListHasPageSizeOrGrater() =
        ((_favoritesState.value as? ScreenState.Success)?.data?.size ?: 0) >= PAGE_SIZE

    private fun simulateLoading() {
        viewModelScope.launch {
            delay(LOADING_SIMULATION)
            _paginationState.update { FavoritePagination.State.Idle }
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val PAGE_INITIALIZATION = -1
    }
}