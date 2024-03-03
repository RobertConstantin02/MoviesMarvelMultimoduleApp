package com.example.usecase.character.implementation

import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCharacterIsFavoriteUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository,
    @DispatcherIO dispatcher: CoroutineDispatcher
) : IUpdateCharacterIsFavoriteUseCase(dispatcher) {
    override suspend fun run(input: Params): Flow<DomainResource<Unit>> =
        repository.updateCharacterIsFavorite(input.isFavorite, input.characterId)

}