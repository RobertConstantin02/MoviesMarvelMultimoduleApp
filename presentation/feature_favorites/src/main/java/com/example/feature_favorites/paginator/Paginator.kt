package com.example.feature_favorites.paginator

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class Paginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoading: () -> Unit,
    private inline val onRequest: suspend (nextPage: Key) -> Flow<List<Item>>,
    private inline val getNextKey: () -> Key,
    private inline val onSuccess: (items: List<Item>) -> Unit,
    private inline val onError: suspend (error: Throwable) -> Unit = {} //suspend because we are using channel which is a suspend function as well
) : IPaginator {

    private var currentPage = initialKey

    override suspend fun loadNextData() {
        onLoading()
            Log.d("-----> pagin currentPage", currentPage.toString())
        (onRequest(getNextKey()).collectLatest { newItems ->
            Log.d("-----> collectNewItems", newItems.toString())

            onSuccess(newItems)
//            pageResult.fold(
//                ifLeft = { onError(it) }
//            ) { newItems ->
//                onSuccess(newItems)
//            }

            //onLoad(false)
        })
    }

    override fun reset() {
        currentPage = initialKey //0 by default from state
    }

}