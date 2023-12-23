package com.example.data_repository.character

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import arrow.core.left
import arrow.core.right
import com.example.core.apiDbBoundResource
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.local.DatabaseUnifiedError
import com.example.core.remote.Resource
import com.example.data_mapper.DtoToCharacterDetailBoMapper.toCharacterDetailBo
import com.example.data_mapper.DtoToCharacterEntityMapper.toCharacterEntity
import com.example.data_mapper.EntityToCharacterBoMapper.toCharacterBo
import com.example.data_mapper.EntityToCharacterBoMapper.toCharacterDetailBo
import com.example.data_mapper.EntityToCharacterBoMapper.toCharacterNeighborBo
import com.example.data_mapper.toCharacterNeighborBo
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.paging.FeedRemoteMediator
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.resources.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000

class CharacterRepository @Inject constructor(
    private val remoteDataSource: ICharacterRemoteDataSource,
    private val localDatabaseDatasource: ICharacterLocalDatasource,
    private val sharedPreferenceDataSource: ISharedPreferenceDataSource
) : ICharacterRepository {

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    override fun getAllCharacters(): Flow<PagingData<CharacterBo>> =
        Pager(
            config = PagingConfig(10),
            remoteMediator = FeedRemoteMediator(localDatabaseDatasource, remoteDataSource),
            pagingSourceFactory = { localDatabaseDatasource.getAllCharacters() }
        ).flow.mapLatest { pagingData ->
            pagingData.map { character -> character.toCharacterBo() }
        }

    override fun getCharactersByIds(charactersIds: List<Int>): Flow<Resource<List<CharacterNeighborBo>>> =
       apiDbBoundResource(
           fetchFromLocal = { localDatabaseDatasource.getCharactersByIds(charactersIds) },
           shouldMakeNetworkRequest = { databaseResult ->
               System.currentTimeMillis() - sharedPreferenceDataSource.getTime() >= DAY_IN_MILLIS
           },
           makeNetworkRequest = { remoteDataSource.getCharactersByIds(charactersIds) },
           saveApiData = { characters ->
               localDatabaseDatasource.insertCharacters(characters?.map { it.toCharacterEntity() } ?: emptyList())
           },
           mapApiToDomain = { characterDtoList ->
               characterDtoList?.map { characterDto -> characterDto.toCharacterNeighborBo() } ?: emptyList()

           },
           mapLocalToDomain = {characterEntityList ->
               characterEntityList.map { characterEntity -> characterEntity.toCharacterNeighborBo() }
           }
       )



    override fun getCharacter(characterId: Int): Flow<Resource<CharacterDetailBo>> {
        return apiDbBoundResource(
            fetchFromLocal = { localDatabaseDatasource.getCharacterById(characterId) },
            shouldMakeNetworkRequest = { databaseResult ->
                System.currentTimeMillis() - sharedPreferenceDataSource.getTime() >= DAY_IN_MILLIS
                        && (databaseResult !is DatabaseResponseSuccess)
            },
            makeNetworkRequest = { remoteDataSource.getCharacterById(characterId) },
            saveApiData = { characterDto ->
                localDatabaseDatasource.insertCharacter(characterDto.toCharacterEntity())
            },
            mapApiToDomain = { characterDto -> characterDto.toCharacterDetailBo() },
            mapLocalToDomain = { characterEntity -> characterEntity.toCharacterDetailBo() }
        )
    }

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ) = when(val localResponse = localDatabaseDatasource.updateCharacterIsFavorite(isFavorite, characterId)) {
        is DatabaseResponseSuccess -> Resource.success(Unit)
        is DatabaseResponseError -> Resource.error(
            localResponse.databaseUnifiedError.messageResource,
            null
        )
        is DatabaseResponseEmpty -> Resource.successEmpty()
    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    override fun getFavoriteCharacters(): Flow<PagingData<CharacterBo>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                enablePlaceholders = false,
//                initialLoadSize = 10
//            ),
//        ) {
//            FavoritesPagingSource(localDatabaseDatasource)
//        }.flow.mapLatest { pagingData ->
//            pagingData.map { character -> character.toCharacterBo() }
//        }
//    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavoriteCharacters(page: Int, offset: Int): Flow<Result<List<CharacterBo>>> {
//        return localDatabaseDatasource.getFavoriteCharacters(offset = page * offset).mapLatest { characters ->
//            characters.map { character -> character.toCharacterBo() }
//        }
        return localDatabaseDatasource.getFavoriteCharacters(offset = page * offset)
            .mapLatest { result ->
                result.fold(ifLeft = { error -> error.left() }) { characters ->
                    characters.map { it.toCharacterBo() }.right()
                }
            }
    }

//        localDatabaseDatasource.getFavoriteCharacters(offset = page * offset).flatMapLatest { result ->
//            flow {
//                result.fold(
//                    ifLeft = { it.left() }
//                ) { characters ->
//                    characters.map {
//                        it.toCharacterBo().right()
//                    }
//                }
//            }
//        }
}