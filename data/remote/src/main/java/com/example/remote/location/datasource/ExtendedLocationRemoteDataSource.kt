package com.example.remote.location.datasource

import com.example.api.model.location.ExtendedLocationDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponse
import com.example.remote.util.apiCall
import com.example.resources.Result
import javax.inject.Inject

class ExtendedLocationRemoteDataSource @Inject constructor(private val service: RickAndMortyService) :
    IExtendedLocationRemoteDataSource {
    override suspend fun getLocation(locationId: Int): ApiResponse<ExtendedLocationDto> =
        service.getLocation(locationId)
}