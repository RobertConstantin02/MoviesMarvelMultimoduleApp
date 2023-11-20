package com.example.usecase.character

import com.example.resources.Result
import com.example.usecase.UseCase
import com.example.usecase.UseCaseNoOutput

interface IUpdateCharacterIsFavoriteUseCase: UseCase<UpdateParams, Unit>

data class UpdateParams(val isFavorite: Boolean, val characterId: Int): UseCaseNoOutput.Input