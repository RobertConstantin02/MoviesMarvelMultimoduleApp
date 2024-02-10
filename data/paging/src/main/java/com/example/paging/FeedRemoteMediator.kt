package com.example.paging

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.PAGE_PARAMETER
import com.example.core.local.localResource
import com.example.core.remote.networkResource
import com.example.data_mapper.DtoToCharacterEntityMapper.toCharacterEntity
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator @Inject constructor(
    private val localDataSource: ICharacterLocalDatasource,
    private val remoteDataSource: ICharacterRemoteDataSource
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
                val remoteKeys = getPagingKeysForLastItem(state)
                val nextPage = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                Uri.parse(nextPage).getQueryParameter(PAGE_PARAMETER)?.toInt()
            }
        }
        return handleCacheSystem(page ?: 1)
    }

    private suspend fun handleCacheSystem(page: Int): MediatorResult =
        networkResource(
            makeNetworkRequest = { remoteDataSource.getAllCharacters(page) }
        ).state.fold(
            success = {
                insertPagingKeys(it)
                insertCharacters(it)
                MediatorResult.Success(endOfPaginationReached = it.results?.isEmpty() == true)
            },
            error = { MediatorResult.Error(Throwable()) },
            empty = { MediatorResult.Error(Throwable()) }
        )

    private suspend fun insertPagingKeys(response: FeedCharacterDto) = with(response) {
        results?.filterNotNull()?.mapNotNull { character ->
            character.id?.toLong()?.let { id ->
                PagingKeys(id, info?.prev.orEmpty(), info?.next.orEmpty())
            }
        }?.also { localDataSource.insertPagingKeys(it) }
    }

    private suspend fun insertCharacters(response: FeedCharacterDto) = with(response) {
        results?.filterNotNull()?.filter { it.id != null }?.map { characterResponse ->
            localResource {
                localDataSource.getCharacterById(characterResponse.id ?: -1)
            }.state.fold(
                success = { characterEntity ->
                    characterResponse.copy(isFavorite = characterEntity.isFavorite)
                },
                error = { characterResponse },
                empty = { characterResponse }
            )
        }.also { characters ->
            if (characters?.isNotEmpty() == true) {
                localDataSource.insertCharacters(characters.map { it.toCharacterEntity() })
            }
        }
    }

    private suspend fun getPagingKeysForLastItem(state: PagingState<Int, CharacterEntity>): PagingKeys? =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()
            ?.let { item -> localDataSource.getPagingKeysById(item.id.toLong()) }
}