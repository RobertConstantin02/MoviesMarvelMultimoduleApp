package com.example.presentation_model

data class CharacterPresentationScreenVO(
    val characterMainDetail: CharacterDetailVO?,
    val extendedLocation: ExtendedLocationVO?,
    val neighbors: List<CharacterNeighborVO>?,
    val episodes: List<EpisodeVO>?
)
