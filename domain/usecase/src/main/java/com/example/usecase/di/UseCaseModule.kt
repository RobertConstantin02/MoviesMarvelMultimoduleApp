package com.example.usecase.di

import com.example.usecase.character.implementation.GetAllCharactersUseCase
import com.example.usecase.character.IGetAllCharactersUseCase
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import com.example.usecase.character.implementation.GetCharacterDetailsUseCaseUseCase
import com.example.usecase.character.implementation.GetFavoriteCharactersUseCaseUseCase
import com.example.usecase.character.implementation.UpdateCharacterIsFavoriteUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GetCharacters

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GetCharacterDetails

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UpdateCharacterIsFavorite

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GetFavoriteCharacters

@Module(includes = [UseCaseModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    /**
     * For example if I have a separate module in topLevel I will create a UseCaseModule and inside
     * modules with each use case based on the feature. For example FeedUseCaseDeclarations. Myabe try.
     */
    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {

        @GetCharacters
        @Singleton
        @Binds
        fun bindGetAllCharactersUseCase(implementation: GetAllCharactersUseCase): IGetAllCharactersUseCase

        @GetCharacterDetails
        @Singleton
        @Binds
        fun bindGetCharacterDetailsUseCase(implementation: GetCharacterDetailsUseCaseUseCase): IGetCharacterDetailsUseCase

        @UpdateCharacterIsFavorite
        @Singleton
        @Binds
        fun bindUpdateCharacterIsFavorite(implementation: UpdateCharacterIsFavoriteUseCase): IUpdateCharacterIsFavoriteUseCase

        @GetFavoriteCharacters
        @Singleton
        @Binds
        fun bindGetFavoriteCharacters(implementation: GetFavoriteCharactersUseCaseUseCase): IGetFavoriteCharactersUseCase
    }
}