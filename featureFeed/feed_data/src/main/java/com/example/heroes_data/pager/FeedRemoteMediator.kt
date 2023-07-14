package com.example.heroes_data.pager

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.heroes_data.api.datasource.CharacterRemoteDataSource
import com.example.heroes_data.db.detasource.CharacterLocalDatasource
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val characterLocalDatasource: CharacterLocalDatasource,
    private val characterRemoteDataSource: CharacterRemoteDataSource
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {

            }
        }
    }

    private suspend fun getRemoteKeysForLastItem(state: PagingState<Int, CharacterEntity>): PagingKeys? =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { item -> characterLocalDatasource.getPagingKeysById(item.id.toLong()) }
}