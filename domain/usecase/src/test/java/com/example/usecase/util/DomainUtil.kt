package com.example.usecase.util

import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.location.ExtendedLocationBo
import com.example.usecase.util.GsonAdapterExt.fromJson

const val CHARACTERS_JSON = "json/characters.json"
const val EXTENDED_LOCATION_JSON = "json/location.json"
const val NEIGHBORS_JSON = "json/neighbors.json"
const val EPISODES_JSON = "json/episodes.json"
object DomainUtil {
    //character
    val charactersDetail = FileUtil.getJson(CHARACTERS_JSON)?.fromJson<Array<CharacterDetailBo>>() as Array<CharacterDetailBo>

    val extendedLocation = FileUtil.getJson(EXTENDED_LOCATION_JSON)?.fromJson<Array<ExtendedLocationBo>>() as Array<ExtendedLocationBo>

    val neighbors = FileUtil.getJson(NEIGHBORS_JSON)?.fromJson<Array<CharacterNeighborBo>>() as Array<CharacterNeighborBo>

    val episodes = FileUtil.getJson(EPISODES_JSON)?.fromJson<Array<EpisodeBo>>() as Array<EpisodeBo>
}