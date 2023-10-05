package com.example.database.dao.location

import androidx.room.TypeConverter
import com.example.database.entities.ExtendedLocationEntity
import com.google.gson.Gson

class ExtendedLocationConverter {
    @TypeConverter
    fun extendedLocationToJson(extendedLocationEntity: ExtendedLocationEntity) =
        Gson().toJson(extendedLocationEntity)

    @TypeConverter
    fun jsonToExtendedLocation(extendedLocationString: String) =
        Gson().fromJson(extendedLocationString, ExtendedLocationEntity::class.java)
}