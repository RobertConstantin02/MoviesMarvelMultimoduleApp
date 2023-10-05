package com.example.presentation_mapper

import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.location.LocationBo
import com.example.presentation_model.CharacterDetailVO
import com.example.presentation_model.CharacterNeighborVO
import com.example.presentation_model.CharacterPresentationScreenVO
import com.example.presentation_model.EpisodeVO
import com.example.presentation_model.ExtendedLocationVO

object BoToVoCharacterPresentationMapper {
    fun CharacterPresentationScreenBO.toCharacterPresentationScreenVO() =
        CharacterPresentationScreenVO(
            characterMainDetail?.toCharacterDetailVO(),
            extendedLocation?.toExtendedLocationVO(),
            neighbors.toCharactersNeighborVO(),
            episodes?.toEpisodesVO()
        )

    private fun CharacterDetailBo.toCharacterDetailVO() =
        CharacterDetailVO(
            id,
            name,
            status,
            specimen,
            location?.toLocationVO(),
            originName,
            gender,
            image?.value.orEmpty(),
            episodes
        )

    private fun LocationBo.toLocationVO() =
        CharacterDetailVO.LocationVO(locationId, url, name)

    private fun ExtendedLocationBo.toExtendedLocationVO() =
        ExtendedLocationVO(id, name, type, dimension, residents)

    private fun List<CharacterNeighborBo>?.toCharactersNeighborVO() =
        this?.map { neighBor -> neighBor.toCharacterNeighborVO() }

    private fun CharacterNeighborBo.toCharacterNeighborVO() =
        CharacterNeighborVO(id, image.value.orEmpty())

    private fun List<EpisodeBo>?.toEpisodesVO() = this?.map { episode -> episode.toEpisodeVO() }

    private fun EpisodeBo.toEpisodeVO() = EpisodeVO(id, name, episode, date)
}