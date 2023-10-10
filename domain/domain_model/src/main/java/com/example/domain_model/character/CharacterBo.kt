package com.example.domain_model.character

import com.example.domain_model.common.ImageUrlBo

data class CharacterBo(
    override val id: Int,
    val locationId: Int?,
    override val image: ImageUrlBo,
    val name: String?,
    val isFavorite: Boolean?
): ICharacterBO
