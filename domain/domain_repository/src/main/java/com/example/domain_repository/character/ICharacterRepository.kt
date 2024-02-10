package com.example.domain_repository.character

import androidx.paging.PagingData
import com.example.core.Resource
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.flow.Flow


interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterBo>>
    fun getCharacter(characterId: Int): Flow<DomainResource<CharacterDetailBo>>
    fun getCharactersByIds(charactersIds: List<Int>): Flow<DomainResource<List<CharacterNeighborBo>>>
    suspend fun updateCharacterIsFavorite(isFavorite: Boolean, characterId: Int): Flow<DomainResource<Unit>>
    fun getFavoriteCharacters(page: Int, offset: Int): Flow<DomainResource<List<CharacterBo>>>
}