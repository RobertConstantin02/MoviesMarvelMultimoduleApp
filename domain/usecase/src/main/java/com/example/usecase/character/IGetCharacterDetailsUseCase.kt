package com.example.usecase.character

import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.usecase.UseCase

interface IGetCharacterDetailsUseCase:  UseCase<IGetCharacterDetailsUseCase.Params, CharacterPresentationScreenBO> {
    data class Params(val characterId: Int, val locationId: Int)
}


