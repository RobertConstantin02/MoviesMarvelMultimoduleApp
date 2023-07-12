package com.example.heroes_data.network

import com.example.heroes_data.model.FeedCharacterDto
import com.example.heroes_data.network.EndPoints.GET_ALL_CHARACTERS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyService {
    @GET(GET_ALL_CHARACTERS)
    fun getAllCharacters(@Query("page") page: Int): Response<FeedCharacterDto>
}