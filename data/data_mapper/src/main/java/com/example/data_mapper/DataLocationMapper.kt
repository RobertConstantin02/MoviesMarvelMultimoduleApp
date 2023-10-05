package com.example.data_mapper

import com.example.api.model.location.ExtendedLocationDto
import com.example.database.entities.ExtendedLocationEntity
import com.example.domain_model.location.ExtendedLocationBo

fun ExtendedLocationDto.toExtendedLocationBo() =
    ExtendedLocationBo(id, name, type, dimension, residents)

fun ExtendedLocationDto.toExtendedLocationEntity() =
    ExtendedLocationEntity(id, name, type, dimension, residents)

fun ExtendedLocationEntity.toExtendedLocationBo() =
    ExtendedLocationBo(id, name, type, dimension, residents)