package com.example.api.model.character

import com.example.api.model.location.LocationDto
import com.example.api.model.location.OriginDto
import com.google.gson.annotations.SerializedName

data class CharacterDto(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("status")
    val status: String?,
    @field:SerializedName("species")
    val specimen: String?,
    @field:SerializedName("location")
    val location: LocationDto?,
    @field:SerializedName("origin")
    val origin: OriginDto?,
    @field:SerializedName("gender")
    val gender: String?,
    @field:SerializedName("image")
    val image: String?,
    @field:SerializedName("episode")
    val episodes: List<String?>?,
    @Transient
    val isFavorite: Boolean = false
)
