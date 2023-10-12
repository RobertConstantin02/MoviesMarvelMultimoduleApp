package com.example.remote.location.datasource

import com.example.api.model.location.ExtendedLocationDto
import com.example.resources.Result

interface IExtendedLocationRemoteDataSource {
    suspend fun getLocation(locationId: Int): Result<ExtendedLocationDto>
}