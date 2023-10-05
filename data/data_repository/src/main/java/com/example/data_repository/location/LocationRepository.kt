package com.example.data_repository.location

import arrow.core.left
import arrow.core.right
import com.example.data_mapper.toExtendedLocationBo
import com.example.data_mapper.toExtendedLocationEntity
import com.example.database.detasource.location.IExtendedLocationLocalDataSource
import com.example.database.entities.ExtendedLocationEntity
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_repository.location.ILocationRepository
import com.example.remote.location.datasource.IExtendedLocationRemoteDataSource
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val remote: IExtendedLocationRemoteDataSource,
    private val local: IExtendedLocationLocalDataSource
) : ILocationRepository {

    override fun getExtendedLocation(extendedLocationId: Int): Flow<Result<ExtendedLocationBo>> =
        flow {
            local.getExtendedLocation(extendedLocationId).fold(
                ifLeft = {
                    remote.getLocation(extendedLocationId).fold(
                        ifLeft = { it.left() }
                    ) { extendedLocationResult ->
                        local.insertExtendedLocation(extendedLocationResult.toExtendedLocationEntity())
                            .onRight {
                                emit(getLocalExtendedLocation(extendedLocationId).onLeft {
                                    emit(extendedLocationResult.toExtendedLocationBo().right())
                                })
                            }.onLeft { emit(extendedLocationResult.toExtendedLocationBo().right()) }
                    }
                }
            ) { extendedLocationEntity ->
                extendedLocationEntity.toExtendedLocationBo().right()
            }
        }

    private suspend fun getLocalExtendedLocation(extendedLocationId: Int) =
        local.getExtendedLocation(extendedLocationId).fold(
            ifLeft = { it.left() }
        ) { extendedLocationEntity ->
            extendedLocationEntity.toExtendedLocationBo().right()
        }
}