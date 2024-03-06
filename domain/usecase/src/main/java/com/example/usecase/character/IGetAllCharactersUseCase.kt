package com.example.usecase.character

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.usecase.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher

abstract class IGetAllCharactersUseCase(dispatcher: CoroutineDispatcher): FlowUseCase<Unit, PagingData<CharacterBo>>(dispatcher)