package com.example.heroes_data.api.model

import com.google.gson.annotations.SerializedName

data class FeedCharacterDto(
    @field:SerializedName("results")
    val results: List<CharacterDto?>?,
    @field:SerializedName("info")
    val info: CharacterInfoDto?,
)

data class CharacterInfoDto(
    @field:SerializedName("next")
    val next: String?,
    @field:SerializedName("pages")
    val pages: Int?,
    @field:SerializedName("prev")
    val prev: String?,
    @field:SerializedName("count")
    val count: Int?
)

data class CharacterDto(
    @field:SerializedName("id")
    val id: Int?,
    @field:SerializedName("image")
    val image: String?,
    @field:SerializedName("gender")
    val gender: String?,
    @field:SerializedName("species")
    val species: String?,
    @field:SerializedName("created")
    val created: String?,
    @field:SerializedName("origin")
    val origin: OriginDto?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("location")
    val location: LocationDto?,
    @field:SerializedName("episode")
    val episode: List<String?>?,
    @field:SerializedName("type")
    val type: String?,
    @field:SerializedName("url")
    val url: String?,
    @field:SerializedName("status")
    val status: String?,
    @Transient
    val isFavorite: Boolean = false
)

data class OriginDto(
    @field:SerializedName("name")
    val name: String?,
)

data class LocationDto(
    @field:SerializedName("name")
    val name: String?,
)