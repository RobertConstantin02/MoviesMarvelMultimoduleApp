package com.example.heroes_data.pager

import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.heroes_data.api.datasource.ICharacterRemoteDataSource
import com.example.heroes_data.api.model.FeedCharacterDto
import com.example.heroes_data.db.detasource.ICharacterLocalDatasource
import com.example.heroes_data.mapper.DtoToEntityCharacterMapper.toCharactersEntity
import com.example.heroes_data.api.network.PAGE_PARAMETER
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
                val nextKey =
                    getPagingKeysForLastItem(state)?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                Uri.parse(nextKey).getQueryParameter(PAGE_PARAMETER)?.toInt()
            }
        }
        return handleCacheSystem(page ?: 1) //test ?: 1
    }

    private suspend fun handleCacheSystem(page: Int): MediatorResult =
        remoteDataSource.getAllCharacters(page).fold(
            ifLeft = { error -> return@fold MediatorResult.Error(error) },
            ifRight = {response ->
                insertPagingKeys(response)
                insertCharacters(response)
                return@fold MediatorResult.Success(endOfPaginationReached = response.results?.isEmpty() == true)
            }
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
            // TODO: Check -1 and transient from dto if works or not. If not create variable oppening brackets
            localDataSource.getCharacterById(characterResponse.id ?: -1)?.let { characterEntity ->
                characterResponse.copy(isFavorite = characterEntity.isFavorite)
            } ?: characterResponse
        }.let { characters ->
            if (characters?.isNotEmpty() == true) {
                localDataSource.insertCharacters(characters.toCharactersEntity())
            }
        }
    }

    private suspend fun getPagingKeysForLastItem(state: PagingState<Int, CharacterEntity>): PagingKeys? =
        state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()
            ?.let { item -> localDataSource.getPagingKeysById(item.id.toLong()) }
}