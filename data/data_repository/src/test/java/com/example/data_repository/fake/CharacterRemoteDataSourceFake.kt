package com.example.data_repository.fake

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.core.remote.ApiResponse
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.ApiUnifiedError
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.test.character.CharacterUtil

class CharacterRemoteDataSourceFake : ICharacterRemoteDataSource {

    private var characters: MutableList<CharacterDto> = mutableListOf()

    var remoteError: ApiResponseError<Unit>? = null
    fun setCharacters(characters: List<CharacterDto>) {
        this.characters = characters.toMutableList()
    }
    override suspend fun getAllCharacters(page: Int): ApiResponse<FeedCharacterDto> {
        remoteError?.let { return getApiError<FeedCharacterDto>() }
        return ApiResponseSuccess(CharacterUtil.expectedSuccessCharacters)
    }

    override suspend fun getCharacterById(characterId: Int): ApiResponse<CharacterDto> {
        remoteError?.let { return getApiError<CharacterDto>() }
        return characters.find { characterDto ->
            characterDto.id == characterId
        }?.let { ApiResponseSuccess(it) } ?: ApiResponseEmpty()
    }

    override suspend fun getCharactersByIds(characterIds: List<Int>): ApiResponse<List<CharacterDto>?> {
        remoteError?.let { return getApiError() }
        return characters.filter { characterDto ->
            characterDto.id in characterIds
        }.let { charactersByIds ->
            if (charactersByIds.isEmpty()) ApiResponseEmpty()
            else ApiResponseSuccess(charactersByIds)
        }
    }

    private fun <T> getApiError(): ApiResponseError<T> =
        when (remoteError?.apiUnifiedError) {
            is ApiUnifiedError.Connectivity.HostUnreachable -> ApiResponseError(ApiUnifiedError.Connectivity.HostUnreachable("Hos Unreachable"))
            is ApiUnifiedError.Connectivity.NoConnection -> ApiResponseError(ApiUnifiedError.Connectivity.NoConnection("No connection"))
            is ApiUnifiedError.Connectivity.TimeOut -> ApiResponseError(ApiUnifiedError.Connectivity.TimeOut("Time out"))
            is ApiUnifiedError.Http.BadRequest -> ApiResponseError(ApiUnifiedError.Http.BadRequest("Bad request"))
            is ApiUnifiedError.Http.EmptyResponse -> ApiResponseError(ApiUnifiedError.Http.EmptyResponse("Empty response"))
            is ApiUnifiedError.Http.InternalErrorApi -> ApiResponseError(ApiUnifiedError.Http.InternalErrorApi("Internal error"))
            is ApiUnifiedError.Http.NotFound -> ApiResponseError(ApiUnifiedError.Http.NotFound("Not found"))
            is ApiUnifiedError.Http.Unauthorized -> ApiResponseError(ApiUnifiedError.Http.Unauthorized("Unauthorized"))
            else -> ApiResponseError(ApiUnifiedError.Generic("Generic error"))
        }
}