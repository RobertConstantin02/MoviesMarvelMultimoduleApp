package com.example.database.dao.episode

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.EpisodeEntity

@Dao
interface IEpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(vararg episodes: EpisodeEntity): LongArray

    @Query("SELECT * FROM episode_entity WHERE episode_entity.id IN (:episodesId)")
    suspend fun getEpisodes(episodesId: List<Int>): List<EpisodeEntity>?
}