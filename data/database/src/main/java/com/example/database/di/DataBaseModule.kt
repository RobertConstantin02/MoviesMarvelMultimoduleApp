package com.example.heroes_data.db.di

import android.content.Context
import com.example.database.RickMortyDatabase
import com.example.database.dao.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.heroes_data.db.detasource.CharacterLocalDatasource
import com.example.heroes_data.db.detasource.ICharacterLocalDatasource
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
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: RickMortyDatabase): ICharacterDao = database.characterDao()

    @Provides
    @Singleton
    fun providePagingKeysDao(database: RickMortyDatabase): IPagingKeysDao = database.pagingKeysDao()

    @Provides
    @Singleton
    fun provideRickMortyDatabase(
        @ApplicationContext context: Context,
    ): RickMortyDatabase = RickMortyDatabase.create(context)

}