package com.example.remote.episode.datasource

import com.example.api.model.episode.EpisodeDto
import com.example.core.remote.ApiResponse

interface IEpisodeRemoteDataSource {

    suspend fun getEpisodesByIds(episodeIds: List<Int>): ApiResponse<List<EpisodeDto?>?>
}