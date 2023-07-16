package com.example.heroes_data.api.datasource

import com.example.heroes_data.api.model.FeedCharacterDto
import com.example.resources.Result

interface ICharacterRemoteDataSource {
    suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto>
}