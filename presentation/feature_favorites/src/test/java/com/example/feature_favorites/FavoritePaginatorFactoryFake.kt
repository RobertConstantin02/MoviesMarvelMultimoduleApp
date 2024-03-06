package com.example.feature_favorites

import com.example.domain_model.resource.DomainResource
import com.example.feature_favorites.paginator.Configuration
import com.example.feature_favorites.paginator.IPaginator
import com.example.feature_favorites.paginator.PaginatorFactory
import com.example.presentation_model.CharacterVo
import com.example.test.character.CharacterUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take

class FavoritePaginatorFactoryFake : PaginatorFactory() {
    override fun <key, Item> createPaginator(configuration: Configuration<key, Item>): IPaginator =
        FavoritePaginatorFake(configuration)
}

class FavoritePaginatorFake<Key, Item>(
    val configuration: Configuration<Key, Item>
) : IPaginator {
    override suspend fun loadNextData() = with(configuration) {
        var page: Int
        onLoading()
        (onRequest(getNextKey().also { page = it as Int }))
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