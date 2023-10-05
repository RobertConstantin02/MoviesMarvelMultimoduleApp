package com.example.api.model.episode

import com.google.gson.annotations.SerializedName

data class EpisodeDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("episode")
    val episode: String?,
    @SerializedName("air_date")
    val date: String?,
)