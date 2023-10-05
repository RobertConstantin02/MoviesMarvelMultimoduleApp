package com.example.domain_repository.character

import androidx.paging.PagingData
import com.example.domain_model.character.ICharacterBOBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow


interface ICharacterRepository {
    fun getAllCharacters(): Flow<PagingData<ICharacterBOBo>>
    fun getCharacter(characterId: Int): Flow<Result<CharacterDetailBo>>
    fun getCharactersByIds(charactersIds: List<Int>): Flow<Result<List<CharacterNeighborBo>>>
}