package com.example.heroes_domain.usecase.implementation

import androidx.paging.PagingData
import com.example.heroes_domain.model.CharacterFeedBo
import com.example.heroes_domain.repository.ICharacterRepository
import com.example.heroes_domain.repository.di.QCharacterRepository
import com.example.heroes_domain.usecase.IGetAllCharactersUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository,
): IGetAllCharactersUseCase {
    override fun invoke(): Flow<PagingData<CharacterFeedBo>> =  repository.getAllCharacters()
}