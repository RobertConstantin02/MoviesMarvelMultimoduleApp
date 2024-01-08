package com.example.database.detasource.episode

import android.database.sqlite.SQLiteException
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseUnifiedError
import com.example.database.dao.episode.IEpisodeDao
import com.example.database.entities.EpisodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EpisodeLocalDataSource @Inject constructor(
    private val dao: IEpisodeDao
): IEpisodeLocalDataSource {
    override suspend fun getEpisodes(episodesId: List<Int>): Flow<DatabaseResponse<List<EpisodeEntity>>> =
        flow {
            try {
                with(dao.getEpisodes(episodesId)) {
                    if (this?.isEmpty() == true) emit(DatabaseResponseEmpty())
                    else emit(DatabaseResponse.create(this))
                }
            }catch (e: SQLiteException) {
                emit(DatabaseResponse.create(DatabaseUnifiedError.Reading))
            }
        }


    override suspend fun insertEpisodes(episodes: List<EpisodeEntity>): DatabaseResponse<Unit> =
        try {
            with(dao.insertEpisodes(*episodes.toTypedArray()) ) {
                if (this.size == episodes.size) DatabaseResponse.create(Unit)
                else DatabaseResponse.create(DatabaseUnifiedError.Insertion)
            }
        }catch (e: SQLiteException) {
            DatabaseResponse.create(DatabaseUnifiedError.Reading)
        }

}