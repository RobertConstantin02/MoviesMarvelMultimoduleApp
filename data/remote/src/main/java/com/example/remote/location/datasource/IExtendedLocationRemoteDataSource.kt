package com.example.remote.location.datasource

import com.example.api.model.location.ExtendedLocationDto
import com.example.core.remote.ApiResponse

interface IExtendedLocationRemoteDataSource {
    suspend fun getLocation(locationId: Int): ApiResponse<ExtendedLocationDto>
}