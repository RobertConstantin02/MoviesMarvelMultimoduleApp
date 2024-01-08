package com.example.database.detasource.location

import com.example.core.local.DatabaseResponse
import com.example.database.entities.ExtendedLocationEntity
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow

interface IExtendedLocationLocalDataSource {
    suspend fun getExtendedLocation(extendedLocationId: Int): Flow<DatabaseResponse<ExtendedLocationEntity>>
    suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): DatabaseResponse<Unit>
}