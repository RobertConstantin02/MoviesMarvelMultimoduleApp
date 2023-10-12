package com.example.remote.character.datasource

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.resources.Result

interface ICharacterRemoteDataSource {
    suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto>
    suspend fun getCharacterById(characterId: Int): Result<CharacterDto>
    suspend fun getCharactersByIds(characterIds: List<Int>): Result<List<CharacterDto>?>
}