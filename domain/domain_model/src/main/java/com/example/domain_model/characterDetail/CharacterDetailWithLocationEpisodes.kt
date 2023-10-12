package com.example.domain_model.characterDetail

import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.location.ExtendedLocationBo

data class CharacterPresentationScreenBO(
    val characterMainDetail: CharacterDetailBo?,
    val extendedLocation: ExtendedLocationBo?,
    val neighbors: List<CharacterNeighborBo>?,
    val episodes: List<EpisodeBo>?
)

data class CharacterWithLocation(
    val characterMainDetail: Pair<CharacterDetailBo?, List<String?>?>, //character and apisodesIds urls
    val extendedLocation: Pair<ExtendedLocationBo?, List<String>?>, //extendedLocation with residentsIds urls
)