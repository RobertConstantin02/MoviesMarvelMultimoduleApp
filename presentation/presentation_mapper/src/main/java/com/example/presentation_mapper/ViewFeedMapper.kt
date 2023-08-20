package com.example.presentation_mapper

import com.example.domain_model.CharacterFeedBo
import com.example.presentation_model.CharacterVo

fun CharacterFeedBo.toUI() =
    CharacterVo(
        id ?: -1,
        image ?: "", // create value object like WamImage
        name ?: "",
        isFavorite ?: false
    )

