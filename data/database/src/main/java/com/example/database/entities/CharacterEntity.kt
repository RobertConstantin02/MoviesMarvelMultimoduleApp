package com.example.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_entity"
)
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val status: String?,
    val specimen: String?,
    val location: LocationEntity?,
    val origin: OriginEntity?,
    val gender: String?,
    val image: String?,
    val episodes: List<String?>?,
    @ColumnInfo("is_Favorite")
    val isFavorite: Boolean = false
)

data class OriginEntity(
    val name: String?,
)

data class LocationEntity(
    val name: String?,
    val url: String?
)