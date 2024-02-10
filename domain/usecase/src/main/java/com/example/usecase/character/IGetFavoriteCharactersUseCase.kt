package com.example.usecase.character

import com.example.core.Resource
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.resource.DomainResource
import com.example.usecase.FlowUseCase

const val OFFSET = 10
interface IGetFavoriteCharactersUseCase : FlowUseCase<IGetFavoriteCharactersUseCase.Params, DomainResource<List<CharacterBo>>> {
    data class Params(val page: Int, val offset: Int = OFFSET)
}

