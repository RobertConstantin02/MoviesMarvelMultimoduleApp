package com.example.remote.location.di

import com.example.remote.character.di.CharacterApiModule
import com.example.remote.di.NetworkModule
import com.example.remote.location.datasource.ExtendedLocationRemoteDataSource
import com.example.remote.location.datasource.IExtendedLocationRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TODO: check if removing NetworkModule works
@Module(includes = [CharacterApiModule.Declarations::class, NetworkModule::class])
@InstallIn(SingletonComponent::class)
object ExtendedLocationApiModule {
    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {
        @Singleton
        @Binds
        fun bindsRemoteExtendedLocationDataSource(implementation: ExtendedLocationRemoteDataSource): IExtendedLocationRemoteDataSource
    }
}