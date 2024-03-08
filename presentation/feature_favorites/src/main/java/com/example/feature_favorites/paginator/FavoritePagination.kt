package com.example.feature_favorites.paginator

import com.example.common.paginatorFactory.IPaginator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile

class FavoritePagination(
    private val configuration: FavoritePagingConfig
) : IPaginator {

    private var currentPage = configuration.initialKey
    private var stopCollecting: Boolean = false

    override suspend fun loadNextData() = with(configuration) {
        if (stopCollecting) stopCollecting = false
        onLoading()
        (onRequest(getNextKey()).takeWhile { !stopCollecting }
            .collectLatest { newItems ->
                newItems.domainState.fold(
                    success = { onSuccess(it) },
                    error = { onError(it.error) },
                    empty = { onEmpty() }
                )
            })
    }

    override fun reset() {
        currentPage = configuration.initialKey
    }

    override fun stopCollection() {
        stopCollecting = true
    }

    sealed class State {
        object Idle : State()
        object Loading : State()
        object End : State()
    }
}