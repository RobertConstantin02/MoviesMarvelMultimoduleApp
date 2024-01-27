package com.example.remote.episode.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.api.model.episode.EpisodeDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.UnifiedError
import com.example.remote.util.CharacterUtil
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private val EPISODES_IDS = listOf(1, 2, 3)
private val BAD_EPISODES_IDS = listOf(1, 2, 3)
class EpisodeRemoteDataSourceTest {
    private lateinit var episodeRemoteDataSource: EpisodeRemoteDataSource
    private lateinit var service: RickAndMortyService

    @BeforeEach
    fun setUp() {
        service = mockk()
        episodeRemoteDataSource = EpisodeRemoteDataSource(service)
    }

    @Test
    fun `service get episodes by id, returns ApiResponseSuccess`() = runTest {
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
        val expected = ApiResponseError<List<EpisodeDto>>(UnifiedError.Http.InternalError("Internal server error"))
        coEvery {
            service.getEpisodesByIds(BAD_EPISODES_IDS)
        } returns ApiResponseError(UnifiedError.Http.InternalError("Internal server error"))
        //When
        val result = episodeRemoteDataSource.getEpisodesByIds(BAD_EPISODES_IDS)
        //Then
        assertThat(result).isEqualTo(expected)
    }


}