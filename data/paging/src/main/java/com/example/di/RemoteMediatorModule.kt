package com.example.di

import com.example.heroes_data.db.detasource.ICharacterLocalDatasource
import com.example.paging.FeedRemoteMediator
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteMediatorModule {
    @Provides
    @Singleton
    fun provideMediator(
        localDatasource: ICharacterLocalDatasource,
        remoteDataSource: ICharacterRemoteDataSource,
    ) = FeedRemoteMediator(localDatasource, remoteDataSource)
}