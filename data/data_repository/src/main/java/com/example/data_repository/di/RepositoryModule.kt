package com.example.data_repository.di

import com.example.data_repository.character.CharacterRepository
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
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