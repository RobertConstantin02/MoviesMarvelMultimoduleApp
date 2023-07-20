package com.example.heroes_presentation.mapper

import com.example.heroes_domain.model.CharacterFeedBo
import com.example.heroes_presentation.feed_screen.model.CharacterVo

internal fun CharacterFeedBo.toUI() =
    CharacterVo(
        id ?: -1,
        image ?: "", // create value object like WamImage
        name ?: "",
        isFavorite ?: false
    )

