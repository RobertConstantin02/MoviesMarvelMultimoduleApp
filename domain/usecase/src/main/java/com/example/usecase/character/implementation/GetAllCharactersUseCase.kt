package com.example.usecase.character.implementation

import androidx.paging.PagingData
import com.example.domain_model.character.ICharacterBOBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IGetAllCharactersUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository,
): IGetAllCharactersUseCase {
    override fun invoke(): Flow<PagingData<ICharacterBOBo>> =  repository.getAllCharacters()
}