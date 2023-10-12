package com.example.api.model.character

import com.google.gson.annotations.SerializedName

data class FeedCharacterDto(
    @SerializedName("results")
    val results: List<CharacterDto?>?,
    @SerializedName("info")
    val info: CharacterInfoDto?,
)

data class CharacterInfoDto(
    @SerializedName("next")
    val next: String?,
    @SerializedName("pages")
    val pages: Int?,
    @SerializedName("prev")
    val prev: String?,
    @SerializedName("count")
    val count: Int?
)





