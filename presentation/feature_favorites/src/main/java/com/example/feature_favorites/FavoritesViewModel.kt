package com.example.feature_favorites

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @GetFavoriteCharacters private val getFavoriteCharacters: IGetFavoriteCharactersUseCase,
    @UpdateCharacterIsFavorite private val updateCharacterIsFavorite: IUpdateCharacterIsFavoriteUseCase
) : ViewModel() {

    private val _favoritesState =
        MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)
    val favoritesState = _favoritesState.asStateFlow()

    private val initialPage = 0

    //var favoritesState: StateFlow<PagingData<CharacterVo>> = getCharactersFav()

//    fun getFavorites() {
//        getFavoriteCharacters.invoke(
//            Unit,
//            viewModelScope,
//            Dispatchers.IO,
//            success = { newData ->
//                _favoritesState.value = newData.map { it.toCharacterVo() }
//            },
//            error = {
//
//            }
//        )
//    }

//    fun getCharactersFav() = getFavoriteCharacters.invoke(
//        Unit,
//        Dispatchers.IO,
//    ).map { pagingData ->
//        pagingData.fold(
//            ifLeft = { PagingData.empty() }
//        ) { pagingData ->
//            pagingData.map { character -> character.toCharacterVo() }
//        }
//    }.cachedIn(viewModelScope).stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = PagingData.empty()
//    )
//
//    fun onEvent(event: FavoritesScreenEvent) {
//        when(event) {
//            is FavoritesScreenEvent.ListFound -> {
//                (_favoritesState.value as? FavoritesScreenState.Success)?.let { successState ->
//
//                }
//            }
//            is FavoritesScreenEvent.Error -> {
//
//            }
//        }
//    }

    fun updateCharacter(isFavorite: Boolean, characterId: Int) {
        updateCharacterIsFavorite.invoke(
            UpdateParams(isFavorite, characterId),
            viewModelScope,
            Dispatchers.IO
        )
//        _favoritesState.value = _favoritesState.value.filter {
//            it.id != characterId
//        }
    }

    private val pagination = Paginator(
        initialKey = initialPage,
        onLoad = {
            _favoritesState.update { FavoritesScreenState.Loading }
        },
        onRequest = { nextPage ->
            getFavoriteCharacters.invoke(
                FavoritesParams(nextPage),
                Dispatchers.IO
            )
        },
        getNextKey = { initialPage + 1 },
        onSuccess = { newCharacters ->
            _favoritesState.update {
                FavoritesScreenState.Success()
            }


            onSuccess(newCharacters.map { it.toCharacterVo() })
        },
        onError = { error -> }
    )

    private fun onSuccess(newCharacters: List<CharacterVo>) {
        (_favoritesState.value as? FavoritesScreenState.Success)?.let { successState ->
            if ((successState.favoriteCharacters + newCharacters).isEmpty()) {
                _favoritesState.update {
                    FavoritesScreenState.Empty(UiText.StringResources(R.string.empty_favorite_list))
                }
            } else {
                _favoritesState.update {
                    FavoritesScreenState.Success(
                        successState.favoriteCharacters + newCharacters,
                        newCharacters.isEmpty()
                    )
                }
            }
        }
    }

    fun loadNextCharacters() {
        viewModelScope.launch { pagination.loadNextData() }
    }

//    private fun onError(error: DataBaseError) {
//        val errorEvent =
//        onEvent(CharacterDetailPSEvent.Error(errorEvent))
//    }

//    private fun checkLocalDbError(error: DataBaseError) =
//        when(error) {
//            is DataBaseError.EmptyResult -> getLocalDbErrorMessage(R.string.local_db_empty_result)
//            else -> {
//                getLocalDbErrorMessage(R.string.local_db_empty_result)
//            }
//        }

}