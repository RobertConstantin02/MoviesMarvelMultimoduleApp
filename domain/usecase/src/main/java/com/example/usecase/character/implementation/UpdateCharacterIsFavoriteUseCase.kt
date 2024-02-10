package com.example.usecase.character.implementation

import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCharacterIsFavoriteUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
) : IUpdateCharacterIsFavoriteUseCase {
    override suspend fun run(input: IUpdateCharacterIsFavoriteUseCase.Params): Flow<DomainResource<Unit>> =
        repository.updateCharacterIsFavorite(input.isFavorite, input.characterId)

}