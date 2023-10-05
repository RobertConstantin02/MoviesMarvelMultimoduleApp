package com.example.presentation_mapper

import com.example.domain_model.character.ICharacterBOBo
import com.example.presentation_model.CharacterVo

fun ICharacterBOBo.toCharacterVo() =
    CharacterVo(
        id ?: -1,
        image.value.orEmpty(),
        name ?: "",
        isFavorite ?: false
    )

