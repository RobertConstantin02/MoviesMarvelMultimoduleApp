package com.example.database.dao.character

import androidx.room.TypeConverter
import com.example.database.entities.CharacterEntity
import com.example.database.entities.LocationEntity
import com.example.database.entities.OriginEntity
import com.google.gson.Gson

class CharacterConverter {

    @TypeConverter
    fun characterEntityToJson(characterEntity: CharacterEntity?): String? =
        Gson().toJson(characterEntity)

    @TypeConverter
    fun jsonToCharacterEntity(string: String?): CharacterEntity? =
        Gson().fromJson(string, CharacterEntity::class.java)

    @TypeConverter
    fun originEntityToJson(originEntity: OriginEntity?): String? =
        Gson().toJson(originEntity)

    @TypeConverter
    fun jsonToOriginEntity(string: String?): OriginEntity? =
        Gson().fromJson(string, OriginEntity::class.java)

    @TypeConverter
    fun locationEntityToJson(locationEntity: LocationEntity?): String? =
        Gson().toJson(locationEntity)

    @TypeConverter
    fun jsonToLocationEntity(string: String?): LocationEntity? =
        Gson().fromJson(string, LocationEntity::class.java)

    @TypeConverter
    fun stringListToJson(stringList: List<String>?): String =
        Gson().toJson(stringList)

    @TypeConverter
    fun jsonToStringList(string: String?): List<String?> =
        Gson().fromJson(string, Array<String>::class.java).toList()



}