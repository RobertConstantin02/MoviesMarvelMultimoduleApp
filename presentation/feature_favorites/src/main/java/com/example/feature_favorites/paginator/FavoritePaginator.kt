package com.example.feature_favorites.paginator

import android.util.Log
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile

class FavoritePaginator<Key, Item>(
    val configuration: Configuration<Key, Item>
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

//create interface to putt whaever configuration we want in each case

data class Configuration<Key, Item>(
    val initialKey: Key,
    inline val onLoading: () -> Unit,
    inline val onRequest: suspend (nextPage: Key) -> Flow<DomainResource<List<Item>>>,
    inline val getNextKey: () -> Key,
    inline val onSuccess: (items: List<Item>) -> Unit,
    inline val onError: (localError: DomainUnifiedError) -> Unit = {},
    inline val onEmpty: () -> Unit = {}
)

abstract class PaginatorFactory {
    abstract fun<key, Item> createPaginator(configuration: Configuration<key, Item>): IPaginator
}

class FavoritePaginatorFactory(): PaginatorFactory() {
    override fun <key, Item> createPaginator(configuration: Configuration<key, Item>) =
        FavoritePaginator(configuration)
}
