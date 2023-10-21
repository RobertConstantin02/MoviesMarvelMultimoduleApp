package com.example.database.detasource.character

import androidx.paging.PagingSource
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow

interface ICharacterLocalDatasource {
    fun getAllCharacters(): PagingSource<Int, CharacterEntity>
    suspend fun getCharacterById(characterId: Int): Result<CharacterEntity>
    suspend fun getCharactersByIds(characterIds: List<Int>): Result<List<CharacterEntity>>
    suspend fun getPagingKeysById(id: Long): PagingKeys?
    suspend fun insertPagingKeys(keys: List<PagingKeys>)
    suspend fun insertCharacters(characters : List<CharacterEntity>): Result<Unit>
    suspend fun insertCharacter(character: CharacterEntity): Result<Unit>
    suspend fun updateCharacterIsFavorite(isFavorite: Boolean, characterId: Int)
    fun getFavoriteCharacters(offset: Int):  Flow<List<CharacterEntity>>
}