package com.example.domain_model.character

import com.example.domain_model.common.ImageUrlBo

data class CharacterNeighborBo(
    override val id: Int,
    override val image: ImageUrlBo,
): ICharacterBO
