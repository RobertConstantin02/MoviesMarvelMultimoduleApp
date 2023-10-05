package com.example.database.detasource.location

import arrow.core.left
import arrow.core.right
import com.example.database.dao.location.IExtendendLocationDao
import com.example.database.entities.ExtendedLocationEntity
import com.example.resources.DataBaseError
import com.example.resources.Result
import javax.inject.Inject

class ExtendedLocationDataSource @Inject constructor(
    private val dao: IExtendendLocationDao
) : IExtendedLocationLocalDataSource {

    override suspend fun getExtendedLocation(extendedLocationId: Int): Result<ExtendedLocationEntity> =
        with(dao.getExtendedLocation(extendedLocationId)) {
            this?.right() ?: DataBaseError.ItemNotFound.left()
        }

    override suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): Result<Unit> =
        if (dao.insertExtendedLocation(extendedLocationEntity) != -1L) Unit.right()
        else DataBaseError.InsertionError.left()
}