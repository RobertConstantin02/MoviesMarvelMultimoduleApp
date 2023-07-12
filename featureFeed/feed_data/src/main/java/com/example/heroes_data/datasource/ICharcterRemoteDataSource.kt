package com.example.heroes_data.datasource

import com.example.heroes_data.model.FeedCharacterDto
import com.example.resources.DataResult

interface ICharcterRemoteDataSource {
    fun getAllCharacters(page: Int): DataResult<FeedCharacterDto>
}