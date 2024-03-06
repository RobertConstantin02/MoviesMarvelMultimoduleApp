package com.example.usecase.character.implementation

import com.example.domain_model.character.CharacterBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteCharactersUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository,
    @DispatcherIO dispatcher: CoroutineDispatcher
) : IGetFavoriteCharactersUseCase(dispatcher) {
    override fun run(input: Params): Flow<DomainResource<List<CharacterBo>>> =
        with(input) { repository.getFavoriteCharacters(page, offset) }
}