package com.example.remote.character.datasource.fake

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.CharacterInfoDto
import com.example.api.model.character.FeedCharacterDto
import com.example.core.remote.ApiResponse
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.ApiUnifiedError
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import java.net.HttpURLConnection

const val API_PAGE_SIZE = 20
const val TEST_ERROR_MESSAGE = "Test Error"
const val QUERY_PAGE = "https://rickandmortyapi.com/api/character/?page="

class CharacterRemoteDataSourceFake : ICharacterRemoteDataSource {

    private var characters: MutableList<CharacterDto> = mutableListOf()

    var remoteError: ApiResponseError<Unit>? = null
    fun setCharacters(characters: List<CharacterDto>) {
        this.characters = characters.toMutableList()
    }

    override suspend fun getAllCharacters(page: Int): ApiResponse<FeedCharacterDto> {
        remoteError?.let { return getApiError() }
        return ApiResponseSuccess(generateCharacterDtoPage(page))
    }

    /**
     * Simulates real paging by giving characters with proper ids by page.
     * @properPage -> needed because api returns first page with 0 for page
     */
    private fun generateCharacterDtoPage(page: Int): FeedCharacterDto {
        val start = (page - 1) * API_PAGE_SIZE + 1
        val characters = mutableListOf<CharacterDto>()
        for (i in start until start + API_PAGE_SIZE) {
            characters.add(
                CharacterDto(
                    id = i,
                    name = "Character $i",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        }
        return FeedCharacterDto(
            info = CharacterInfoDto(
                pages = 42,
                next = "$QUERY_PAGE${page + 1}",
                prev = if (page > 1) "$QUERY_PAGE${page - 1}" else null,
                count = 826
            ), results = characters
        )
    }

    override suspend fun getCharacterById(characterId: Int): ApiResponse<CharacterDto> {
        remoteError?.let { return getApiError() }
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
            is ApiUnifiedError.Connectivity.HostUnreachable -> ApiResponseError(
                ApiUnifiedError.Connectivity.HostUnreachable(
                    TEST_ERROR_MESSAGE
                )
            )

            is ApiUnifiedError.Connectivity.NoConnection -> ApiResponseError(
                ApiUnifiedError.Connectivity.NoConnection(
                    TEST_ERROR_MESSAGE
                )
            )

            is ApiUnifiedError.Connectivity.TimeOut -> ApiResponseError(
                ApiUnifiedError.Connectivity.TimeOut(
                    TEST_ERROR_MESSAGE
                )
            )

            is ApiUnifiedError.Http.BadRequest -> ApiResponseError(
                ApiUnifiedError.Http.BadRequest(
                    TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_BAD_REQUEST
                )
            )

            is ApiUnifiedError.Http.InternalErrorApi -> ApiResponseError(
                ApiUnifiedError.Http.InternalErrorApi(
                    TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_INTERNAL_ERROR
                )
            )

            is ApiUnifiedError.Http.NotFound -> ApiResponseError(
                ApiUnifiedError.Http.NotFound(
                    TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_NOT_FOUND
                )
            )

            is ApiUnifiedError.Http.Unauthorized -> ApiResponseError(
                ApiUnifiedError.Http.Unauthorized(
                    TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_UNAUTHORIZED
                )
            )

            else -> ApiResponseError(ApiUnifiedError.Generic(TEST_ERROR_MESSAGE))
        }
}