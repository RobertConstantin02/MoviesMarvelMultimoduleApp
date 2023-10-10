package com.example.presentation_mapper

import com.example.domain_model.character.CharacterBo
import com.example.presentation_model.CharacterVo

fun CharacterBo.toCharacterVo() =
    CharacterVo(
        id ?: -1,
        locationId,
        image.value.orEmpty(),
        name ?: "",
        isFavorite ?: false
    )

