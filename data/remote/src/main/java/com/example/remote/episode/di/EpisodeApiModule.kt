package com.example.remote.episode.di

import com.example.remote.character.di.CharacterApiModule
import com.example.remote.di.NetworkModule
import com.example.remote.episode.datasource.EpisodeRemoteDataSource
import com.example.remote.episode.datasource.IEpisodeRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TODO: check if removing NetworkModule works
@Module(includes = [CharacterApiModule.Declarations::class, NetworkModule::class])
@InstallIn(SingletonComponent::class)
object EpisodeApiModule {
    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {
        @Singleton
        @Binds
        fun bindsRemoteEpisodeDataSource(implementation: EpisodeRemoteDataSource): IEpisodeRemoteDataSource
    }
}