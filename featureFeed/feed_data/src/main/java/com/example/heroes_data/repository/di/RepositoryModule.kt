package com.example.heroes_data.repository.di

import com.example.heroes_data.repository.CharacterRepository
import com.example.heroes_domain.repository.ICharacterRepository
import com.example.heroes_domain.repository.di.QCharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @QCharacterRepository
    @Singleton
    @Binds
    fun bindCharacterRepository(implementation: CharacterRepository): ICharacterRepository

}