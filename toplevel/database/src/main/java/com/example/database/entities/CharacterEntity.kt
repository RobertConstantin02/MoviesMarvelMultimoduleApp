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
    val image: String?,
    val gender: String?,
    val species: String?,
    val created: String?,
    val origin: OriginEntity?,
    val name: String?,
    val location: LocationEntity?,
    val episode: List<String?>?,
    val type: String?,
    val url: String?,
    val status: String?,
    @ColumnInfo("is_Favorite")
    val isFavorite: Boolean = false
)

data class OriginEntity(
    val name: String?,
)

data class LocationEntity(
    val name: String?,
)