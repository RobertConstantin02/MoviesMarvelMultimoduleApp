package com.example.presentation_model

data class CharacterDetailVO(
    val id: Int?,
    val name: String?,
    val status: String?,
    val specimen: String?,
    val location: LocationVO?,
    val originName: String?,
    val gender: String?,
    val image: String?,
    val episodes: List<String?>?,
) {
    data class LocationVO(
        val id: Int?,
        val url: String?,
        val name: String?
    )
}

