package com.example.remote.character.di

import com.example.api.network.RickAndMortyService
import com.example.remote.character.datasource.CharacterRemoteDataSource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.remote.di.NetworkModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [CharacterApiModule.Declarations::class, NetworkModule::class]) //be careful because NetworkModule is in an other module so we have to include it.
@InstallIn(SingletonComponent::class)
object CharacterApiModule {

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