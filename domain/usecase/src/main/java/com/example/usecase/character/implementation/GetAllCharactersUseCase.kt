package com.example.usecase.character.implementation

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.IGetAllCharactersUseCase
import com.example.usecase.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository,
    @DispatcherIO dispatcher: CoroutineDispatcher
): IGetAllCharactersUseCase(dispatcher) {
    override fun run(input: Unit): Flow<PagingData<CharacterBo>> =
        repository.getAllCharacters()
}