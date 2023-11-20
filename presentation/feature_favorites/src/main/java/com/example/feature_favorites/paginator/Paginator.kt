package com.example.feature_favorites.paginator

import com.example.resources.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.takeWhile

class Paginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoading: () -> Unit,
    private inline val onRequest: suspend (nextPage: Key) -> Flow<Result<List<Item>>>,
    private inline val getNextKey: () -> Key,
    private inline val onSuccess: (items: List<Item>) -> Unit,
    private inline val onError: suspend (error: Throwable) -> Unit = {}
) : IPaginator {

    private var currentPage = initialKey
    private var stopCollecting: Boolean = false

    override suspend fun loadNextData() {
        if (stopCollecting) stopCollecting = false
        onLoading()
        (onRequest(getNextKey()).takeWhile { !stopCollecting }
            .collectLatest { newItems -> newItems.fold(ifLeft = { onError(it) }) {
                onSuccess(it)
            } })
    }

    override fun reset() { currentPage = initialKey }

    fun stopCollection() { stopCollecting = true }

    sealed class State {
        object Idle : State()
        object Loading : State()
        object End : State()
    }
}

