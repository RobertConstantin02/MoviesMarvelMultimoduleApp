package com.example.usecase.character

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.usecase.FlowUseCase

interface IGetAllCharactersUseCase: FlowUseCase<Unit, PagingData<CharacterBo>>