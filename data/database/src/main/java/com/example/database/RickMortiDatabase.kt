package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.character.CharacterConverter
import com.example.database.dao.character.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.dao.episode.EpisodeConverter
import com.example.database.dao.episode.IEpisodeDao
import com.example.database.dao.location.ExtendedLocationConverter
import com.example.database.dao.location.IExtendendLocationDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.EpisodeEntity
import com.example.database.entities.ExtendedLocationEntity
import com.example.database.entities.PagingKeys

private const val DATABASE_NAME = "rick_morty_database"

@Database(
    entities = [ CharacterEntity::class, PagingKeys::class, EpisodeEntity::class, ExtendedLocationEntity::class ],
    version = 1
)

@TypeConverters(CharacterConverter::class, EpisodeConverter::class, ExtendedLocationConverter::class)
abstract class RickMortyDatabase: RoomDatabase() {
    abstract fun characterDao(): ICharacterDao
    abstract fun pagingKeysDao(): IPagingKeysDao
    abstract fun episodeDao(): IEpisodeDao

    abstract fun extendedLocationDao(): IExtendendLocationDao

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