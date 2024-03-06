package com.example.usecase.character

import com.example.domain_model.character.CharacterBo
import com.example.domain_model.resource.DomainResource
import com.example.usecase.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher

const val OFFSET = 10
abstract class IGetFavoriteCharactersUseCase(dispatcher: CoroutineDispatcher) : FlowUseCase<IGetFavoriteCharactersUseCase.Params, DomainResource<List<CharacterBo>>>(dispatcher) {
    data class Params(val page: Int, val offset: Int = OFFSET)
}

