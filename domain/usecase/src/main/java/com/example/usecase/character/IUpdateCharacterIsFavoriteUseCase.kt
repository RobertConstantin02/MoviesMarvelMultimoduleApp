package com.example.usecase.character

import com.example.usecase.UseCaseNoOutput

interface IUpdateCharacterIsFavoriteUseCase: UseCaseNoOutput<UpdateParams>

data class UpdateParams(val isFavorite: Boolean, val characterId: Int): UseCaseNoOutput.Input