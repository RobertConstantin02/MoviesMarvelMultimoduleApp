package com.example.data_repository.character

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data_mapper.EntityToBoCharacterMapper.toCharacterBo
import com.example.domain_model.CharacterFeedBo
import com.example.heroes_data.db.detasource.ICharacterLocalDatasource
import com.example.paging.FeedRemoteMediator
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.domain_repository.character.ICharacterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val remoteDataSource: ICharacterRemoteDataSource,
    private val localDatasource: ICharacterLocalDatasource
): ICharacterRepository {

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    override fun getAllCharacters(): Flow<PagingData<CharacterFeedBo>>  =
        Pager(
            config = PagingConfig(10),
            remoteMediator = FeedRemoteMediator(localDatasource, remoteDataSource),
            pagingSourceFactory = { localDatasource.getAllCharacters() }
        ).flow.mapLatest { pagingData ->
            pagingData.map { character ->  character.toCharacterBo()}
        }
}