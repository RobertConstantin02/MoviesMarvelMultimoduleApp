package com.example.data_repository.character

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import arrow.core.left
import arrow.core.right
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
import kotlinx.coroutines.flow.flatMapLatest
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

    override fun getCharactersByIds(charactersIds: List<Int>): Flow<Result<List<CharacterNeighborBo>>> =
        flow {
            if (System.currentTimeMillis() - sharedPreferenceDataSource.getTime() >= DAY_IN_MILLIS) {
                getApiCharactersByIds(charactersIds)
                sharedPreferenceDataSource.saveCurrentTimeMs()
            } else {
                localDatabaseDatasource.getCharactersByIds(charactersIds).fold(
                    ifLeft = { getApiCharactersByIds(charactersIds) }
                ) { charactersEntity ->
                    emit(charactersEntity.map { it.toCharacterNeighborBo() }.right())
                }
            }
        }

    // TODO: see if pagination is not fucked up after that insertion. Se also if we have to use flowOn
    override fun getCharacter(characterId: Int): Flow<Result<CharacterDetailBo>> = flow {
        if (System.currentTimeMillis() - sharedPreferenceDataSource.getTime() >= DAY_IN_MILLIS) {
            emit(getApiCharacter(characterId))
        } else {
            localDatabaseDatasource.getCharacterById(characterId).fold(
                ifLeft = { emit(getApiCharacter(characterId)) }
            ) { characterEntity -> emit(characterEntity.toCharacterDetailBo().right()) }
        }
    }

    private suspend fun FlowCollector<Result<List<CharacterNeighborBo>>>.getApiCharactersByIds(
        charactersIds: List<Int>
    ) {
        remoteDataSource.getCharactersByIds(charactersIds).fold(
            ifLeft = { emit(it.left()) },
        ) { charactersResult ->
            charactersResult?.let { characters ->
                if (characters.isNotEmpty()) {
                    localDatabaseDatasource.insertCharacters(characters.map { it.toCharacterEntity() })
                        .onRight {
                            emit(getLocalCharacters(charactersIds).onLeft {
                                emit(characters.map { it.toCharacterNeighborBo() }.right())
                            })
                        }.onLeft {
                            emit(characters.map { it.toCharacterNeighborBo() }.right())
                        }
                }
            }
        }
    }

    private suspend fun getLocalCharacters(charactersId: List<Int>) =
        localDatabaseDatasource.getCharactersByIds(charactersId).fold(
            ifLeft = { it.left() }
        ) { charactersEntity ->
            charactersEntity.map {
                it.toCharacterNeighborBo()
            }.right()
        }

    private suspend fun getApiCharacter(characterId: Int): Result<CharacterDetailBo> =
        remoteDataSource.getCharacterById(characterId).fold(
            ifLeft = { it.left() }
        ) { characterDto ->
            localDatabaseDatasource.insertCharacter(characterDto.toCharacterEntity()).fold(
                ifLeft = { characterDto.toCharacterDetailBo().right() }
            ) {
                localDatabaseDatasource.getCharacterById(characterId).fold(
                    ifLeft = { characterDto.toCharacterDetailBo().right() }
                ) {
                    it.toCharacterDetailBo().right()
                }
            }

        }

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ) = flow { emit(localDatabaseDatasource.updateCharacterIsFavorite(isFavorite, characterId)) }


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
                result.fold(ifLeft = {error -> error.left()}) { characters ->
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