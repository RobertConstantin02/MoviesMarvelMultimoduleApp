package com.example.api.network

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.api.model.episode.EpisodeDto
import com.example.api.model.location.ExtendedLocationDto
import com.example.api.network.CharacterEndPoints.GET_ALL_CHARACTERS
import com.example.api.network.CharacterEndPoints.GET_CHARACTER
import com.example.api.network.CharacterEndPoints.GET_CHARACTERS_BY_IDS
import com.example.api.network.EpisodeEndPoints.GET_EPISODES_BY_IDS
import com.example.api.network.LocationEndPoints.GET_LOCATION
import com.example.core.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val PAGE_PARAMETER = "page"
const val ID_PATH = "id"
const val IDS_PATH = "ids"
interface RickAndMortyService {
    @GET(GET_ALL_CHARACTERS)
    suspend fun getAllCharacters(@Query(PAGE_PARAMETER) page: Int): ApiResponse<FeedCharacterDto>

    @GET(GET_CHARACTER)
    suspend fun getCharacter(@Path(ID_PATH) characterId: Int): ApiResponse<CharacterDto>

    @GET(GET_LOCATION)
    suspend fun getLocation(@Path(ID_PATH) locationId: Int): ApiResponse<ExtendedLocationDto>

    @GET(GET_EPISODES_BY_IDS)
    suspend fun getEpisodesByIds(@Path(IDS_PATH) episodesId: List<Int>): ApiResponse<List<EpisodeDto?>?>

    @GET(GET_CHARACTERS_BY_IDS)
    suspend fun getCharactersByIds(@Path(IDS_PATH) episodesId: List<Int>): ApiResponse<List<CharacterDto>?>
}