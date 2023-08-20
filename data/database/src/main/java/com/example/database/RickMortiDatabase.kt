package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.CharacterConverter
import com.example.database.dao.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys

private const val DATABASE_NAME = "rick_morty_database"

@Database(
    entities = [ CharacterEntity::class, PagingKeys::class ],
    version = 1
)

@TypeConverters(CharacterConverter::class)
abstract class RickMortyDatabase: RoomDatabase() {
    abstract fun characterDao(): ICharacterDao
    abstract fun pagingKeysDao(): IPagingKeysDao

    companion object {
        fun create(context: Context): RickMortyDatabase {
            return Room
                .databaseBuilder(
                    context,
                    RickMortyDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                .build()
        }
    }
}