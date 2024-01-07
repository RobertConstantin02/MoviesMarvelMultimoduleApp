package com.example.usecase.character.implementation

import android.util.Log
import com.example.core.remote.Resource
import com.example.domain_model.character.CharacterBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.resources.Result
import com.example.usecase.character.FavoritesParams
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetFavoriteCharactersUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val repository: ICharacterRepository
) : IGetFavoriteCharactersUseCase {
    override fun run(input: FavoritesParams): Flow<Resource<List<CharacterBo>>> = with(input) {
        Log.d("-----> currentPage", page.toString())

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