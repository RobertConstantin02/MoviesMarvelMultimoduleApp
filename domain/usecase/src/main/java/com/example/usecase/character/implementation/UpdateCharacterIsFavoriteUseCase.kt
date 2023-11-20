package com.example.usecase.character.implementation

import arrow.core.left
import arrow.core.right
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.resources.Result
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.character.UpdateParams
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class UpdateCharacterIsFavoriteUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
) : IUpdateCharacterIsFavoriteUseCase {
    override suspend fun run(input: UpdateParams): Flow<Result<Unit>> =
        repository.updateCharacterIsFavorite(input.isFavorite, input.characterId)

}