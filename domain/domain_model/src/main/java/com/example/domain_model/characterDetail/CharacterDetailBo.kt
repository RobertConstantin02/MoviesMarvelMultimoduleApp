package com.example.domain_model.characterDetail

import com.example.domain_model.common.ImageUrlBo
import com.example.domain_model.location.LocationBo

data class CharacterDetailBo(
    val id: Int?,
    val name: String?,
    val status: String?,
    val specimen: String?,
    val location: LocationBo?,
    val originName: String?,
    val gender: String?,
    val image: ImageUrlBo?,
    val episodes: List<String?>?,
) {
    val locationId = location?.locationId
}