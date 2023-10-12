package com.example.data_repository.di

import com.example.data_repository.character.CharacterRepository
import com.example.data_repository.episode.EpisodeRepository
import com.example.data_repository.location.LocationRepository
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.domain_repository.di.QEpisodesRepository
import com.example.domain_repository.di.QLocationRepository
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.domain_repository.location.ILocationRepository
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
    @QEpisodesRepository
    @Singleton
    @Binds
    fun bindEpisodeRepository(implementation: EpisodeRepository): IEpisodeRepository

    @QLocationRepository
    @Singleton
    @Binds
    fun bindLocationRepository(implementation: LocationRepository): ILocationRepository

}