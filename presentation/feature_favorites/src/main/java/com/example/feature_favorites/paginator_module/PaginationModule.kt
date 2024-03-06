package com.example.feature_favorites.paginator_module

import com.example.feature_favorites.paginator.FavoritePaginatorFactory
import com.example.feature_favorites.paginator.PaginatorFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PaginationModule {
    @Provides
    @ViewModelScoped
    fun providePaginatorFactory(): PaginatorFactory = FavoritePaginatorFactory()
}