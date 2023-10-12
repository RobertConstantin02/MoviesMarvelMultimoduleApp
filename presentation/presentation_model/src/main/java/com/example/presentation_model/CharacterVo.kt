package com.example.presentation_model

data class CharacterVo(
    val id: Int,
    val locationId: Int?,
    val image: String?,
    val name: String?,
    val isFavorite: Boolean = false
)
