package com.example.usecase.character

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import kotlinx.coroutines.flow.Flow

interface IGetAllCharactersUseCase{
    operator fun invoke(): Flow<PagingData<CharacterBo>>
}