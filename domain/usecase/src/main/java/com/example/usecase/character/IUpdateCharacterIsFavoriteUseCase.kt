package com.example.usecase.character

import com.example.usecase.UseCase
import kotlinx.coroutines.CoroutineDispatcher

abstract class IUpdateCharacterIsFavoriteUseCase(dispatcher: CoroutineDispatcher): UseCase<IUpdateCharacterIsFavoriteUseCase.Params, Unit>(dispatcher) {
    data class Params(val isFavorite: Boolean, val characterId: Int): Input
}

