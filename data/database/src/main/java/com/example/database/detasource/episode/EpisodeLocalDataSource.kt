package com.example.database.detasource.episode

import arrow.core.left
import arrow.core.right
import com.example.database.dao.episode.IEpisodeDao
import com.example.database.entities.EpisodeEntity
import com.example.resources.DataBaseError
import com.example.resources.Result
import javax.inject.Inject

class EpisodeLocalDataSource @Inject constructor(
    private val dao: IEpisodeDao
): IEpisodeLocalDataSource {
    override suspend fun getEpisodes(episodesId: List<Int>): Result<List<EpisodeEntity>> =
        with(dao.getEpisodes(episodesId)) {
            if (isNullOrEmpty()) DataBaseError.EmptyResult.left() else this.right()
        }

    override suspend fun insertEpisodes(episodes: List<EpisodeEntity>): Result<Unit> =
        with(dao.insertEpisodes(*episodes.toTypedArray()) ) {
            if (this.size == episodes.size) Unit.right()
            else DataBaseError.InsertionError.left()
        }
}