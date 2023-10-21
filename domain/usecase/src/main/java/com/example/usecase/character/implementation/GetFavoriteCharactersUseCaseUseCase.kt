package com.example.usecase.character.implementation

import com.example.domain_model.character.CharacterBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.usecase.character.FavoritesParams
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoriteCharactersUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
) : IGetFavoriteCharactersUseCase {
    override fun run(input: FavoritesParams): Flow<List<CharacterBo>> = with(input) {
        repository.getFavoriteCharacters(page, offset)
//        repository.getFavoriteCharacters(page, offset).flatMapLatest { result ->
//            result.fold(
//                ifLeft = { flowOf(emptyList()) }
//            ) { characterList ->
//                flow {
//                    characterList
//                }
//            }
//        }
    }
}