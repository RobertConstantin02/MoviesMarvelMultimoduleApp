package com.example.heroes_data.api.datasource

import com.example.heroes_data.api.model.FeedCharacterDto
import com.example.heroes_data.api.network.RickAndMortyService
import com.example.resources.Result
import com.example.resources.apiCall
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val remoteService: RickAndMortyService
): ICharacterRemoteDataSource {

    override suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto> =
        apiCall { remoteService.getAllCharacters(page) }
}