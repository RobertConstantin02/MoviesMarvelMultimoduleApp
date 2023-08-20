package com.example.remote.character.datasource

import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.RickAndMortyService
import com.example.remote.character.util.apiCall
import com.example.resources.Result
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val remoteService: RickAndMortyService
): ICharacterRemoteDataSource {

    override suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto> =
        apiCall { remoteService.getAllCharacters(page) }
}