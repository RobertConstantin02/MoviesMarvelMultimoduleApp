package com.example.data_mapper

import com.example.api.model.character.CharacterDto
import com.example.api.model.character.LocationDto
import com.example.api.model.character.OriginDto
import com.example.database.entities.CharacterEntity
import com.example.database.entities.LocationEntity
import com.example.database.entities.OriginEntity
import com.example.domain_model.CharacterFeedBo

object DtoToEntityCharacterMapper {
    fun List<CharacterDto>.toCharactersEntity() = map { characterDto ->
        with(characterDto) {
            CharacterEntity(
                id ?: -1,
                image,
                gender,
                species,
                created,
                origin.toOriginEntity(),
                name,
                location.toLocationEntity(),
                episode,
                type,
                url,
                status,
                isFavorite
            )
        }
    }

    private fun OriginDto?.toOriginEntity() = this?.let { OriginEntity(name) }

    private fun LocationDto?.toLocationEntity() = this?.let { LocationEntity(name) }
}

object EntityToBoCharacterMapper {
    fun CharacterEntity.toCharacterBo() = CharacterFeedBo(id, image, name, isFavorite)
}

