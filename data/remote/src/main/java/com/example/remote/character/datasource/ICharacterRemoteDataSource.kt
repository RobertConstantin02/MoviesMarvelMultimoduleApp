package com.example.remote.character.datasource

import com.example.api.model.character.FeedCharacterDto
import com.example.resources.Result

interface ICharacterRemoteDataSource {
    suspend fun getAllCharacters(page: Int): Result<FeedCharacterDto>
}