package com.example.domain_repository.episode

import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.flow.Flow

interface IEpisodeRepository {
    fun getEpisodes(episodesIds: List<Int>): Flow<DomainResource<List<EpisodeBo>>>
}