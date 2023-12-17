package com.example.remote.character.datasource

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponse
import com.example.remote.util.apiCall
import com.example.resources.Result
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val remoteService: RickAndMortyService
): ICharacterRemoteDataSource {

    override suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto> =
        apiCall { remoteService.getAllCharacters(page) }

//    override suspend fun getCharacterById(characterId: Int): Result<CharacterDto> =
//        apiCall { remoteService.getCharacter(characterId) }
    override suspend fun getCharacterById(characterId: Int): ApiResponse<CharacterDto> =
        remoteService.getCharacter(characterId)

    override suspend fun getCharactersByIds(characterIds: List<Int>): Result<List<CharacterDto>?> =
        apiCall { remoteService.getCharactersByIds(characterIds) }
}