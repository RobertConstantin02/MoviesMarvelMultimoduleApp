package com.example.usecase.character

import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.usecase.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

abstract class IGetCharacterDetailsUseCase(dispatcher: CoroutineDispatcher):  UseCase<IGetCharacterDetailsUseCase.Params, CharacterPresentationScreenBO>(dispatcher) {
    data class Params(val characterId: Int, val locationId: Int)
}


