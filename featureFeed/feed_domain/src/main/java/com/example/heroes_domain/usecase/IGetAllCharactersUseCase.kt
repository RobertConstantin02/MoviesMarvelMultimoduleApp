package com.example.heroes_domain.usecase

import androidx.paging.PagingData
import com.example.heroes_domain.model.CharacterFeedBo
import com.example.usecase.UseCaseLocal

interface IGetAllCharactersUseCase: UseCaseLocal<Unit, PagingData<CharacterFeedBo>>