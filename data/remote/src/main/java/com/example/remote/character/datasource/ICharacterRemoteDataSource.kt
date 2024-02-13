package com.example.remote.character.datasource

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.core.remote.ApiResponse

interface ICharacterRemoteDataSource {
    suspend fun getAllCharacters(page: Int): ApiResponse<FeedCharacterDto>
    suspend fun getCharacterById(characterId: Int): ApiResponse<CharacterDto>
    suspend fun getCharactersByIds(characterIds: List<Int>): ApiResponse<List<CharacterDto>?>
}