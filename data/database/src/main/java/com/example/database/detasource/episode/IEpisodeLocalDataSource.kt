package com.example.database.detasource.episode

import com.example.database.entities.EpisodeEntity
import com.example.resources.Result

interface IEpisodeLocalDataSource {
    suspend fun getEpisodes(episodesId: List<Int>): Result<List<EpisodeEntity>>
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>): Result<Unit>
}