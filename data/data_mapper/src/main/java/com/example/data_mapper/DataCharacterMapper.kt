package com.example.data_mapper

import com.example.api.model.character.CharacterDto
import com.example.api.model.location.LocationDto
import com.example.api.model.location.OriginDto
import com.example.database.entities.CharacterEntity
import com.example.database.entities.LocationEntity
import com.example.database.entities.OriginEntity
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.common.ImageUrlBo
import com.example.domain_model.location.LocationBo

object DtoToCharacterEntityMapper {
    fun CharacterDto.toCharacterEntity() =
        CharacterEntity(
            id ?: -1,
            name,
            status,
            specimen,
            location.toLocationEntity(),
            origin.toOriginEntity(),
            gender,
            image,
            episodes,
            isFavorite
        )



    private fun OriginDto?.toOriginEntity() = this?.let { OriginEntity(name) }

    private fun LocationDto?.toLocationEntity() = this?.let { LocationEntity(name, url) }
}

object EntityToCharacterBoMapper {

    fun CharacterEntity.toCharacterBo() =
        CharacterBo(id, location.toLocationBo()?.locationId ,ImageUrlBo(image), name, isFavorite)

    fun CharacterEntity.toCharacterDetailBo() = CharacterDetailBo(
        id,
        name,
        status,
        specimen,
        location.toLocationBo(),
        origin?.name,
        gender,
        ImageUrlBo(image),
        episodes
    )
    fun CharacterEntity.toCharacterNeighborBo() = CharacterNeighborBo(id, ImageUrlBo(image))

    private fun LocationEntity?.toLocationBo() = this?.let { LocationBo(url, name) }
}



object DtoToCharacterDetailBoMapper {
    fun CharacterDto.toCharacterDetailBo() =
        CharacterDetailBo(
            id,
            name,
            status,
            specimen,
            location.toLocationBo(),
            origin?.name,
            gender,
            ImageUrlBo(image),
            episodes
        )

    private fun LocationDto?.toLocationBo() = this?.let { LocationBo(url, name) }
}

fun CharacterDto.toCharacterNeighborBo() = CharacterNeighborBo(id, ImageUrlBo(image))

