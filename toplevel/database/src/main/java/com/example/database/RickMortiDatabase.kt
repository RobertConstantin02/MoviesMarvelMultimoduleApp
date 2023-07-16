package com.example.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.database.dao.ICharacterDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys

@Database(
    entities = [ CharacterEntity::class, PagingKeys::class ],
    version = 1
)
/**
 * Each time I access database i want to access the functionality for a given entity
 */
abstract class RickMortyDatabase: RoomDatabase() {
    abstract fun characterDao(): ICharacterDao

}