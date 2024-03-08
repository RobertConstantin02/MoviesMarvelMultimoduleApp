package com.example.feature_favorites.paginator_module

import com.example.common.paginatorFactory.PaginationFactory
import com.example.feature_favorites.paginator.FavoritePaginationFactory
import com.example.feature_favorites.paginator.FavoritePagingConfig
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
    fun provideFavoritePaginationFactory(): PaginationFactory<FavoritePagingConfig> =
        FavoritePaginationFactory()
}