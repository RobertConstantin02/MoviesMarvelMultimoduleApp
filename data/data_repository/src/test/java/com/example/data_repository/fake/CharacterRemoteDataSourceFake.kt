package com.example.data_repository.fake

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.core.remote.ApiResponse
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.UnifiedError
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
        when (remoteError?.unifiedError) {
            is UnifiedError.Connectivity.HostUnreachable -> ApiResponseError(UnifiedError.Connectivity.HostUnreachable("Hos Unreachable"))
            is UnifiedError.Connectivity.NoConnection -> ApiResponseError(UnifiedError.Connectivity.NoConnection("No connection"))
            is UnifiedError.Connectivity.TimeOut -> ApiResponseError(UnifiedError.Connectivity.TimeOut("Time out"))
            is UnifiedError.Http.BadRequest -> ApiResponseError(UnifiedError.Http.BadRequest("Bad request"))
            is UnifiedError.Http.EmptyResponse -> ApiResponseError(UnifiedError.Http.EmptyResponse("Empty response"))
            is UnifiedError.Http.InternalError -> ApiResponseError(UnifiedError.Http.InternalError("Internal error"))
            is UnifiedError.Http.NotFound -> ApiResponseError(UnifiedError.Http.NotFound("Not found"))
            is UnifiedError.Http.Unauthorized -> ApiResponseError(UnifiedError.Http.Unauthorized("Unauthorized"))
            else -> ApiResponseError(UnifiedError.Generic("Generic Error"))
        }
}