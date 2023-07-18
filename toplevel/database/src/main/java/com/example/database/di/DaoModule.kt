package com.example.database.di

import com.example.database.RickMortyDatabase
import com.example.database.dao.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickMortyDatabase): ICharacterDao = database.characterDao()

    @Provides
    @Singleton
    fun providePagingKeysDao(database: RickMortyDatabase): IPagingKeysDao = database.pagingKeysDao()
}