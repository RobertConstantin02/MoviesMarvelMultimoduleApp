package com.example.usecase.character.implementation

import com.example.domain_model.character.CharacterBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteCharactersUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
) : IGetFavoriteCharactersUseCase {
    override fun run(input: IGetFavoriteCharactersUseCase.Params): Flow<DomainResource<List<CharacterBo>>> =
        with(input) { repository.getFavoriteCharacters(page, offset) }
}