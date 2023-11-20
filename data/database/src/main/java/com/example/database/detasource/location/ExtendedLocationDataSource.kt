package com.example.database.detasource.location

import arrow.core.left
import arrow.core.right
import com.example.database.dao.location.IExtendendLocationDao
import com.example.database.entities.ExtendedLocationEntity
import com.example.resources.DataBase
import com.example.resources.Result
import javax.inject.Inject

class ExtendedLocationDataSource @Inject constructor(
    private val dao: IExtendendLocationDao
) : IExtendedLocationLocalDataSource {

    override suspend fun getExtendedLocation(extendedLocationId: Int): Result<ExtendedLocationEntity> =
        with(dao.getExtendedLocation(extendedLocationId)) {
            this?.right() ?: DataBase.EmptyResult.left()
        }

    override suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): Result<Unit> =
        if (dao.insertExtendedLocation(extendedLocationEntity) != -1L) Unit.right()
        else DataBase.Error.Insertion.left()
}