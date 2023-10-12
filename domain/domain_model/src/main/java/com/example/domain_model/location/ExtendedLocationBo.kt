package com.example.domain_model.location

data class ExtendedLocationBo(
    val id: Int?,
    val name: String?,
    val type: String?,
    val dimension: String?,
    val residents: List<String>?
)
