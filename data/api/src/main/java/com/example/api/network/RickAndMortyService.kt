package com.example.api.network

import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.EndPoints.GET_ALL_CHARACTERS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val PAGE_PARAMETER = "page"
interface RickAndMortyService {
    @GET(GET_ALL_CHARACTERS)
    suspend fun getAllCharacters(@Query(PAGE_PARAMETER) page: Int): Response<FeedCharacterDto>
}