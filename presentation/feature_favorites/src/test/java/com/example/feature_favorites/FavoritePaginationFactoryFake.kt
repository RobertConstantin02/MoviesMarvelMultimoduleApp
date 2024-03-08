package com.example.feature_favorites

import com.example.common.paginatorFactory.IPaginator
import com.example.common.paginatorFactory.PaginationFactory
import com.example.feature_favorites.paginator.FavoritePagingConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take

class FavoritePaginationFactoryFake : PaginationFactory<FavoritePagingConfig>() {
    override fun createPagination(configuration: FavoritePagingConfig): IPaginator  =
        FavoritePaginationFake(configuration)
}

class FavoritePaginationFake(
    private val configuration: FavoritePagingConfig
) : IPaginator {
    override suspend fun loadNextData() = with(configuration) {
        var page: Int
        onLoading()
        (onRequest(getNextKey().also { page = it }))
            .take(1).collectLatest { newItems ->
                newItems.domainState.fold(
                    success = {
                        val startIndex = page * 10
                        val endIndex = startIndex + 10
                        if (it.size >= 10) onSuccess(it.subList(startIndex, endIndex))
                        else onSuccess(it)
                    },
                    error = { onError(it.error) },
                    empty = { onEmpty() }
                )
            }
    }

    override fun reset() {

    }

    override fun stopCollection() {

    }
}