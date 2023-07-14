package com.example.database.di

import com.example.database.RickMortyDatabase
import com.example.database.dao.ICharacterDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Singleton
    @Binds
    fun provideCharacterDao(database: RickMortyDatabase): ICharacterDao =
        database.characterDao()
}