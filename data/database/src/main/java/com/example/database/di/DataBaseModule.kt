package com.example.database.di

import android.content.Context
import com.example.database.RickMortyDatabase
import com.example.database.dao.character.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.dao.episode.IEpisodeDao
import com.example.database.dao.location.IExtendendLocationDao
import com.example.database.detasource.character.CharacterLocalDatasource
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.detasource.episode.EpisodeLocalDataSource
import com.example.database.detasource.episode.IEpisodeLocalDataSource
import com.example.database.detasource.location.ExtendedLocationDataSource
import com.example.database.detasource.location.IExtendedLocationLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [DataBaseModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {
        @Singleton
        @Binds
        fun bindsLocalCharacterDataSource(implementation: CharacterLocalDatasource): ICharacterLocalDatasource

        @Singleton
        @Binds
        fun bindsLocalEpisodeDataSource(implementation: EpisodeLocalDataSource): IEpisodeLocalDataSource

        @Singleton
        @Binds
        fun bindsLocalExtendedLocationDataSource(implementation: ExtendedLocationDataSource): IExtendedLocationLocalDataSource
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickMortyDatabase): ICharacterDao = database.characterDao()

    @Provides
    @Singleton
    fun provideEpisodeDao(database: RickMortyDatabase): IEpisodeDao = database.episodeDao()

    @Provides
    @Singleton
    fun provideExtendedLocationDao(database: RickMortyDatabase): IExtendendLocationDao = database.extendedLocationDao()

    @Provides
    @Singleton
    fun providePagingKeysDao(database: RickMortyDatabase): IPagingKeysDao = database.pagingKeysDao()

    @Provides
    @Singleton
    fun provideRickMortyDatabase(
        @ApplicationContext context: Context,
    ): RickMortyDatabase = RickMortyDatabase.create(context)

}