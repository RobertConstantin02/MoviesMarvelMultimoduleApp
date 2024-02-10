package com.example.data_repository.episode

import com.example.core.remote.apiDbBoundResource
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.Resource
import com.example.data_mapper.DtoToEpisodeBo.toEpisodesBo
import com.example.data_mapper.DtoToEpisodeEntityMapper.toEpisodesEntities
import com.example.data_mapper.EntityToEpisodeBoMapper.toEpisodesBo
import com.example.data_mapper.toDomainResource
import com.example.data_repository.character.DAY_IN_MILLIS
import com.example.database.detasource.episode.IEpisodeLocalDataSource
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.remote.episode.datasource.IEpisodeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val remote: IEpisodeRemoteDataSource,
    private val local: IEpisodeLocalDataSource,
    private val sharedPref: ISharedPreferenceDataSource
) : IEpisodeRepository {
    override fun getEpisodes(episodesIds: List<Int>): Flow<DomainResource<List<EpisodeBo>>> =
        apiDbBoundResource(
            fetchFromLocal = {
                local.getEpisodes(episodesIds)
            },
            shouldMakeNetworkRequest = { databaseResult ->
                (databaseResult !is DatabaseResponseSuccess) ||
                        (System.currentTimeMillis() - sharedPref.getTime() > DAY_IN_MILLIS)
            },
            makeNetworkRequest = { remote.getEpisodesByIds(episodesIds) },
            saveApiData = { episodesDto ->
                local.insertEpisodes(episodesDto?.filterNotNull()?.toEpisodesEntities().orEmpty())
            },
            mapApiToDomain = { episodesDto ->
                episodesDto?.filterNotNull()?.toEpisodesBo().orEmpty()
            },
            mapLocalToDomain = { episodesEntity ->
                episodesEntity.toEpisodesBo()
            }
        ).map { resource -> resource.toDomainResource() }
}