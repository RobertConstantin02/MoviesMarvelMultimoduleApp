package com.example.domain_repository.episode

import com.example.domain_model.episode.EpisodeBo
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow

interface IEpisodeRepository {
    fun getEpisodes(episodesIds: List<Int>): Flow<Result<List<EpisodeBo>>>
}