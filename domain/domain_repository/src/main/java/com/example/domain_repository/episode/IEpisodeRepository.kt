package com.example.domain_repository.episode

import com.example.core.Resource
import com.example.domain_model.episode.EpisodeBo
import kotlinx.coroutines.flow.Flow

interface IEpisodeRepository {
    fun getEpisodes(episodesIds: List<Int>): Flow<Resource<List<EpisodeBo>>>
}