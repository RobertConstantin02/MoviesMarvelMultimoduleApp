package com.example.feature_favorites

import android.util.Log
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
import kotlinx.coroutines.Job
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

    private var job: Job? = null

    private var currentPage = -1
    private var currentCharacterList = mutableListOf<CharacterVo>()
    var canPaginate by mutableStateOf(true)

    private val pagination = Paginator(
        initialKey = currentPage,
        onLoading = {
            if (currentPage == 0) _favoritesState.update { FavoritesScreenState.Loading }
            //else _favoritesState.update { FavoritesScreenState.Paging }
        },
        onRequest = { nextPage ->
            Log.d("-----> nextPage", nextPage.toString())
            getFavoriteCharacters.invoke(
                FavoritesParams(currentPage),
                Dispatchers.IO
            )
        },
        getNextKey = {
            currentPage ++
            Log.d("-----> currentPage", (currentPage).toString())
        },
        onSuccess = { newCharacters ->
            canPaginate = newCharacters.size == 10
            // TODO: remove this shit of filtering
            onSuccess(newCharacters.map { it.toCharacterVo() }.filter {
                it !in currentCharacterList
            })
            //no init paging
        },
        onError = { error -> }
    )

    fun updateCharacter(isFavorite: Boolean, characterId: Int, itemIndex: Int) {
        updateCharacterIsFavorite.invoke(
            UpdateParams(isFavorite, characterId),
            viewModelScope,
            Dispatchers.IO
        )
        currentCharacterList.removeAt(itemIndex)
    }

    private fun onSuccess(newCharacters: List<CharacterVo>) {
        //(_favoritesState.value as? FavoritesScreenState.Success)?.let { successState ->
        //Log.d("-----> newCharacters", newCharacters.toString())
        if ((currentCharacterList + newCharacters).isEmpty()) {
            _favoritesState.update {
                FavoritesScreenState.Empty(UiText.StringResources(R.string.empty_favorite_list))
            }
        } else {
            _favoritesState.update {
                FavoritesScreenState.Success(
                    currentCharacterList + newCharacters,
                    //newCharacters.isEmpty()
                )
            }
            currentCharacterList.addAll(newCharacters)
            //_favoritesState.update { FavoritesScreenState.Idle }
        }
        //}
    }

    fun loadNextCharacters() {
        job?.cancel()
        job = viewModelScope.launch { pagination.loadNextData() }
    }
}