package com.example.feature_favorites.paginator

import com.example.common.paginatorFactory.Configuration
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.resource.DomainResource
import kotlinx.coroutines.flow.Flow

data class FavoritePagingConfig(
    val initialKey: Int,
    inline val onLoading: () -> Unit,
    inline val onRequest: suspend (nextPage: Int) -> Flow<DomainResource<List<CharacterBo>>>,
    inline val getNextKey: () -> Int,
    inline val onSuccess: (items: List<CharacterBo>) -> Unit,
    inline val onError: (localError: DomainUnifiedError) -> Unit = {},
    inline val onEmpty: () -> Unit = {}
): Configuration
