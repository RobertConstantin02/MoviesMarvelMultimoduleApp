package com.example.heroes_domain.usecase

import androidx.paging.PagingData
import com.example.heroes_domain.model.CharacterFeedBo
import kotlinx.coroutines.flow.Flow

interface IGetAllCharactersUseCase{
    operator fun invoke(): Flow<PagingData<CharacterFeedBo>>
}