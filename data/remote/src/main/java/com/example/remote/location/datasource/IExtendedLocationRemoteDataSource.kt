package com.example.remote.location.datasource

import com.example.api.model.location.ExtendedLocationDto
import com.example.core.remote.ApiResponse
import com.example.core.remote.Resource
import com.example.resources.Result

interface IExtendedLocationRemoteDataSource {
    suspend fun getLocation(locationId: Int): ApiResponse<ExtendedLocationDto>
}