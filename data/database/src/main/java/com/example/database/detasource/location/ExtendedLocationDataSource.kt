package com.example.database.detasource.location

import android.database.sqlite.SQLiteException
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseUnifiedError
import com.example.database.dao.location.IExtendendLocationDao
import com.example.database.entities.ExtendedLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExtendedLocationDataSource @Inject constructor(
    private val dao: IExtendendLocationDao
) : IExtendedLocationLocalDataSource {

    override suspend fun getExtendedLocation(extendedLocationId: Int): Flow<DatabaseResponse<ExtendedLocationEntity>> =
        flow {
            try {
                with(dao.getExtendedLocation(extendedLocationId)) {
                    emit(DatabaseResponse.create(this))
                }
            }catch (e: SQLiteException) {
                emit(DatabaseResponse.create(DatabaseUnifiedError.Reading))
            }
        }

    override suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): DatabaseResponse<Unit> =
        try {
            if (dao.insertExtendedLocation(extendedLocationEntity) != -1L) DatabaseResponse.create(Unit)
            else DatabaseResponse.create(DatabaseUnifiedError.Insertion)
        } catch (e: SQLiteException) {
            DatabaseResponse.create(DatabaseUnifiedError.Reading)
        }

}