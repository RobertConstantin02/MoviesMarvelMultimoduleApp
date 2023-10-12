package com.example.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_entity")
data class EpisodeEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val episode: String?,
    val date: String?,
)