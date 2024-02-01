package com.example.data_repository.location

import com.example.core.remote.apiDbBoundResource
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.Resource
import com.example.data_mapper.toExtendedLocationBo
import com.example.data_mapper.toExtendedLocationEntity
import com.example.data_repository.character.DAY_IN_MILLIS
import com.example.database.detasource.location.IExtendedLocationLocalDataSource
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_repository.location.ILocationRepository
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.remote.location.datasource.IExtendedLocationRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val remote: IExtendedLocationRemoteDataSource,
    private val local: IExtendedLocationLocalDataSource,
    private val sharedPreferenceDataSource: ISharedPreferenceDataSource
) : ILocationRepository {

    override fun getExtendedLocation(extendedLocationId: Int): Flow<Resource<ExtendedLocationBo>> =
        apiDbBoundResource(
            fetchFromLocal = { local.getExtendedLocation(extendedLocationId) },
            shouldMakeNetworkRequest = { databaseResult ->
                (databaseResult !is DatabaseResponseSuccess) ||
                System.currentTimeMillis() - sharedPreferenceDataSource.getTime() > DAY_IN_MILLIS
            },
            makeNetworkRequest = { remote.getLocation(extendedLocationId) },
            saveApiData = { extendedLocationResult ->
                local.insertExtendedLocation(extendedLocationResult.toExtendedLocationEntity())
            },
            mapApiToDomain = { extLocationDto -> extLocationDto.toExtendedLocationBo() },
            mapLocalToDomain = { extLocationEntity -> extLocationEntity.toExtendedLocationBo() }
        )
}