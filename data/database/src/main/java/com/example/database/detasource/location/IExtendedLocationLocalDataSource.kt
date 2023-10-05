package com.example.database.detasource.location

import com.example.database.entities.ExtendedLocationEntity
import com.example.resources.Result

interface IExtendedLocationLocalDataSource {
    suspend fun getExtendedLocation(extendedLocationId: Int): Result<ExtendedLocationEntity>
    suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): Result<Unit>
}