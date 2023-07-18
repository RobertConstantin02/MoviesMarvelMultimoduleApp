package com.example.heroes_data.db.di

import com.example.heroes_data.api.datasource.ICharacterRemoteDataSource
import com.example.heroes_data.db.detasource.CharacterLocalDatasource
import com.example.heroes_data.db.detasource.ICharacterLocalDatasource
import com.example.heroes_data.pager.FeedRemoteMediator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        fun bindsLocalCharacterDataSource(implementation: CharacterLocalDatasource ): ICharacterLocalDatasource
    }


    @Provides
    @Singleton
    fun provideMediator(
        localDatasource: ICharacterLocalDatasource,
        remoteDataSource: ICharacterRemoteDataSource,
    ) = FeedRemoteMediator(localDatasource, remoteDataSource)
}