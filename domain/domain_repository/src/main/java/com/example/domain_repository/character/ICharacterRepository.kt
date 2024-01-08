package com.example.domain_repository.character

import androidx.paging.PagingData
import com.example.core.Resource
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import kotlinx.coroutines.flow.Flow


interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterBo>>
    fun getCharacter(characterId: Int): Flow<Resource<CharacterDetailBo>>
    fun getCharactersByIds(charactersIds: List<Int>): Flow<Resource<List<CharacterNeighborBo>>>
    suspend fun updateCharacterIsFavorite(isFavorite: Boolean, characterId: Int): Flow<Resource<Unit>>
    fun getFavoriteCharacters(page: Int, offset: Int): Flow<Resource<List<CharacterBo>>>
}