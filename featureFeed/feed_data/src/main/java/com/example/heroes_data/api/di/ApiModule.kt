package com.example.heroes_data.api.di

import com.example.heroes_data.api.datasource.CharacterRemoteDataSource
import com.example.heroes_data.api.datasource.ICharacterRemoteDataSource
import com.example.heroes_data.api.network.RickAndMortyService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [ApiModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object ApiModule {

    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {
        @Singleton
        @Binds
        fun bindsRemoteCharactersDataSource(implementation: CharacterRemoteDataSource): ICharacterRemoteDataSource
    }

    @Provides
    @Singleton
    fun provideRickMortyService(retrofit: Retrofit): RickAndMortyService =
        retrofit.create(RickAndMortyService::class.java)

}