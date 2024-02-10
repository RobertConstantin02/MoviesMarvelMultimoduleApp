package com.example.remote.episode.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.api.model.episode.EpisodeDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.ApiUnifiedError
import com.example.remote.extension.toRickAndMortyService
import com.example.remote.fake.ApiErrorHandlerFake
import com.example.test.character.CharacterUtil
import com.example.test.character.EPISODES_BY_ID_JSON
import com.example.test.FileUtil
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val EPISODES_IDS = listOf(1, 2, 3)
private val BAD_EPISODES_IDS = listOf(1, 2, 3)
class EpisodeRemoteDataSourceTest {
    private lateinit var episodeRemoteDataSource: EpisodeRemoteDataSource
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiErrorHandler: ApiErrorHandlerFake
    private lateinit var service: RickAndMortyService

    @BeforeEach
    fun setUp() {
//        service = mockk()
//        episodeRemoteDataSource = EpisodeRemoteDataSource(service)

        //Testing success url call
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiErrorHandler = ApiErrorHandlerFake()
        service = mockWebServer.toRickAndMortyService(apiErrorHandler)
        episodeRemoteDataSource = EpisodeRemoteDataSource(service)
    }

    //Testing success url call
    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `service get episodes by id, is success url`() = runTest {
        //Given
        val episodesJson = FileUtil.getJson(EPISODES_BY_ID_JSON)
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(episodesJson.orEmpty())
        )
        //When
        episodeRemoteDataSource.getEpisodesByIds(EPISODES_IDS)
        val request = mockWebServer.takeRequest()
        val requestUrl = request.requestUrl
        //Then
        assertThat(request.method).isEqualTo("GET")
        assertThat(getPathIds(requestUrl?.pathSegments)).isEqualTo(EPISODES_IDS)
    }
    private fun getPathIds(pathWithIds: List<String>?) =
        pathWithIds?.let { pathWithIds.last().removeSurrounding("[", "]").split(",").map { it.trim().toInt() }}


    @Test
    fun `service get episodes by id, is ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponseSuccess<List<EpisodeDto>> = ApiResponseSuccess(CharacterUtil.expectedSuccessEpisodesByIds)
        coEvery {
            service.getEpisodesByIds(EPISODES_IDS)
        } returns ApiResponseSuccess(expected.body)
        //When
        val result = episodeRemoteDataSource.getEpisodesByIds(EPISODES_IDS)
        //Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `service get episodes with bad ids, return empty list`() = runTest {
        //GIVEN
        val expected: ApiResponseSuccess<List<EpisodeDto>> = ApiResponseSuccess(emptyList())
        coEvery {
            service.getEpisodesByIds(BAD_EPISODES_IDS)
        } returns ApiResponseSuccess(emptyList())
        //When
        val result = episodeRemoteDataSource.getEpisodesByIds(BAD_EPISODES_IDS)
        //Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `service get episodes by ids, return ApiResponseError`() = runTest {
        //GIVEN
        val expected = ApiResponseError<List<EpisodeDto>>(ApiUnifiedError.Http.InternalErrorApi("Internal server error"))
        coEvery {
            service.getEpisodesByIds(BAD_EPISODES_IDS)
        } returns ApiResponseError(ApiUnifiedError.Http.InternalErrorApi("Internal server error"))
        //When
        val result = episodeRemoteDataSource.getEpisodesByIds(BAD_EPISODES_IDS)
        //Then
        assertThat(result).isEqualTo(expected)
    }
}