package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.CharacterConverter
import com.example.database.dao.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys

@Database(
    entities = [ CharacterEntity::class, PagingKeys::class ],
    version = 1
)

@TypeConverters(CharacterConverter::class)
abstract class RickMortyDatabase: RoomDatabase() {
    abstract fun characterDao(): ICharacterDao
    abstract fun pagingKeysDao(): IPagingKeysDao

}