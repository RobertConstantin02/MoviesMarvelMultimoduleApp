package com.example.heroes_data.mapper

import com.example.database.entities.CharacterEntity
import com.example.database.entities.LocationEntity
import com.example.database.entities.OriginEntity
import com.example.heroes_data.api.model.CharacterDto
import com.example.heroes_data.api.model.LocationDto
import com.example.heroes_data.api.model.OriginDto
import com.example.heroes_domain.model.CharacterFeedBo

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

