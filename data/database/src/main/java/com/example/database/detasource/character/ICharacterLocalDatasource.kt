package com.example.database.detasource.character

import androidx.paging.PagingSource
import com.example.core.local.DatabaseResponse
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import kotlinx.coroutines.flow.Flow

interface ICharacterLocalDatasource {
    fun getAllCharacters(): PagingSource<Int, CharacterEntity>
    suspend fun getCharacterById(characterId: Int): Flow<DatabaseResponse<CharacterEntity>>
    suspend fun getCharactersByIds(characterIds: List<Int>): Flow<DatabaseResponse<List<CharacterEntity>>>
    suspend fun getPagingKeysById(id: Long): PagingKeys?
    suspend fun insertPagingKeys(keys: List<PagingKeys>)
    suspend fun insertCharacters(characters : List<CharacterEntity>): DatabaseResponse<Unit>
    suspend fun insertCharacter(character: CharacterEntity): DatabaseResponse<Unit>
    suspend fun updateCharacterIsFavorite(isFavorite: Boolean, characterId: Int): Flow<DatabaseResponse<Unit>>
    fun getFavoriteCharacters(offset: Int):  Flow<DatabaseResponse<List<CharacterEntity>>>
}