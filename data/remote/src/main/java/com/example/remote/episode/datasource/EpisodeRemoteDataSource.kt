package com.example.remote.episode.datasource

import com.example.api.model.episode.EpisodeDto
import com.example.api.network.RickAndMortyService
import com.example.remote.util.apiCall
import com.example.resources.Result
import javax.inject.Inject

class EpisodeRemoteDataSource @Inject constructor(
    private val service: RickAndMortyService
): IEpisodeRemoteDataSource {

    override suspend fun getEpisodesByIds(episodeIds: List<Int>): Result<List<EpisodeDto?>?> = apiCall {
        service.getEpisodesByIds(episodeIds)
    }
}