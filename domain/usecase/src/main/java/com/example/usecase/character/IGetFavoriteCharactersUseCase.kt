package com.example.usecase.character

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.resources.Result
import com.example.usecase.PagingUseCase
import com.example.usecase.UseCaseLocal

interface IGetFavoriteCharactersUseCase : PagingUseCase<FavoritesParams, List<CharacterBo>>

data class FavoritesParams(val page: Int, val offset: Int = OFFSET)

const val OFFSET = 10