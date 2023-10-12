package com.example.usecase.character.implementation

import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.character.UpdateParams
import javax.inject.Inject

class UpdateCharacterIsFavoriteUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
):  IUpdateCharacterIsFavoriteUseCase {
    override suspend fun run(input: UpdateParams) {
        repository.updateCharacterIsFavorite(input.isFavorite, input.characterId)
    }
}