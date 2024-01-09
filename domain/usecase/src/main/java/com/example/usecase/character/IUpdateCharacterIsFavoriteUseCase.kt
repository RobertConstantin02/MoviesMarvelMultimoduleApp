package com.example.usecase.character

import com.example.usecase.UseCase

interface IUpdateCharacterIsFavoriteUseCase: UseCase<IUpdateCharacterIsFavoriteUseCase.Params, Unit> {
    data class Params(val isFavorite: Boolean, val characterId: Int): UseCase.Input
}

