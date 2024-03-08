package com.example.feature_favorites.paginator

import com.example.common.paginatorFactory.IPaginator
import com.example.common.paginatorFactory.PaginationFactory

class FavoritePaginationFactory: PaginationFactory<FavoritePagingConfig>() {
    override fun createPagination(configuration: FavoritePagingConfig): IPaginator =
        FavoritePagination(configuration)
}