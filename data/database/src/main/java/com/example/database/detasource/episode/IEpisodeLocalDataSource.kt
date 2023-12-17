package com.example.database.detasource.episode

import com.example.core.local.DatabaseResponse
import com.example.database.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow

interface IEpisodeLocalDataSource {
    suspend fun getEpisodes(episodesId: List<Int>): Flow<DatabaseResponse<List<EpisodeEntity>>>
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>): DatabaseResponse<Unit>
}