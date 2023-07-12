package com.example.heroes_data.datasource

import com.example.heroes_data.model.FeedCharacterDto
import com.example.heroes_data.network.RickAndMortyService
import com.example.resources.DataResult
import com.example.resources.apiCall
import javax.inject.Inject

class CharacterRemoteDataSource @Inject constructor(
    private val remoteService: RickAndMortyService
): ICharcterRemoteDataSource {

    override fun getAllCharacters(page: Int): DataResult<FeedCharacterDto> =
        apiCall { remoteService.getAllCharacters(page) }
}