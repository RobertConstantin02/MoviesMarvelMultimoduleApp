package com.example.heroes_presentation.feed_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.heroes_domain.usecase.IGetAllCharactersUseCase
import com.example.heroes_domain.usecase.di.GetCharacters
import com.example.heroes_presentation.feed_screen.model.CharacterVo
import com.example.heroes_presentation.mapper.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    @GetCharacters private val getAllCharacters: IGetAllCharactersUseCase
) : ViewModel() {

    val feedState: Flow<PagingData<CharacterVo>> =
        getAllCharacters.invoke().map {
            it.map { character -> character.toUI() }
        }.cachedIn(viewModelScope)
}
