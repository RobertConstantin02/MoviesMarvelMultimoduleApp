package com.example.api.model.location

import com.google.gson.annotations.SerializedName

data class ExtendedLocationDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("dimension")
    val dimension: String?,
    @SerializedName("residents")
    val residents: List<String>?
)