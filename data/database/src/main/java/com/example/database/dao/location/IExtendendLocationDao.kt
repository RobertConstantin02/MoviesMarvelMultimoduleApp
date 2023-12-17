package com.example.database.dao.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.core.local.DatabaseResponse
import com.example.database.entities.ExtendedLocationEntity

@Dao
interface IExtendendLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtendedLocation(extendedLocationEntity: ExtendedLocationEntity): Long

    @Query("SELECT * FROM extended_location_entity WHERE id = :extendedLocationId")
    suspend fun getExtendedLocation(extendedLocationId: Int): ExtendedLocationEntity?
}