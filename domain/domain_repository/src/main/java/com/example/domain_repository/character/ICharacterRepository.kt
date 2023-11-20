package com.example.domain_repository.character

import androidx.paging.PagingData
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow


interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterBo>>
    fun getCharacter(characterId: Int): Flow<Result<CharacterDetailBo>>
    fun getCharactersByIds(charactersIds: List<Int>): Flow<Result<List<CharacterNeighborBo>>>
    suspend fun updateCharacterIsFavorite(isFavorite: Boolean, characterId: Int): Flow<Result<Unit>>
    fun getFavoriteCharacters(page: Int, offset: Int): Flow<Result<List<CharacterBo>>>
}