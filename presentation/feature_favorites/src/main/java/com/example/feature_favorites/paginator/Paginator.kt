package com.example.feature_favorites.paginator

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile

class Paginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoading: () -> Unit,
    private inline val onRequest: suspend (nextPage: Key) -> Flow<List<Item>>,
    private inline val getNextKey: () -> Key,
    private inline val onSuccess: (items: List<Item>) -> Unit,
    private inline val onError: suspend (error: Throwable) -> Unit = {} //suspend because we are using channel which is a suspend function as well
) : IPaginator {

    private var currentPage = initialKey
    private var stopCollecting: Boolean = false

    override suspend fun loadNextData() {
        if (stopCollecting) stopCollecting = false
        onLoading()
        (onRequest(getNextKey()).takeWhile { !stopCollecting }
            .collectLatest { newItems ->
                Log.d("-----> items", newItems.toString())
                onSuccess(newItems)
            })
    }

    override fun reset() { currentPage = initialKey }

    fun stopCollection() { stopCollecting = true }

}

sealed class PaginationState {

    object Idle : PaginationState()
    object Loading : PaginationState()
    object PaginationEnd : PaginationState()

}