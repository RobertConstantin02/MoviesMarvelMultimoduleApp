package com.example.usecase.character

import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.usecase.UseCaseRemote

interface IGetCharacterDetailsUseCase:  UseCaseRemote<DetailParams, CharacterPresentationScreenBO>

data class DetailParams(val characterId: Int, val locationId: Int)
