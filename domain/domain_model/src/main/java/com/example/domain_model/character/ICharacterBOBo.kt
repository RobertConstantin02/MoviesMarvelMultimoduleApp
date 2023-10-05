package com.example.domain_model.character

import com.example.domain_model.common.ImageUrlBo

data class ICharacterBOBo(
    override val id: Int,
    override val image: ImageUrlBo,
    val name: String?,
    val isFavorite: Boolean?
): ICharacterBO
