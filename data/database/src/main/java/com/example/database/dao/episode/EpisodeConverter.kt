package com.example.database.dao.episode

import androidx.room.TypeConverter
import com.example.database.entities.EpisodeEntity
import com.google.gson.Gson

class EpisodeConverter {
    @TypeConverter
    fun episodesToJson(episodes: List<EpisodeEntity>): String = Gson().toJson(episodes)

    @TypeConverter
    fun jsonToEpisodes(episodesString: String): Array<EpisodeEntity> =
        Gson().fromJson(episodesString, Array<EpisodeEntity>::class.java)
}