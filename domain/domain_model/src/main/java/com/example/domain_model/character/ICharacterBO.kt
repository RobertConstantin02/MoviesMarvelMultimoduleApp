package com.example.domain_model.character

import com.example.domain_model.common.ImageUrlBo

interface ICharacterBO {
    val id: Int
    val image: ImageUrlBo?
}