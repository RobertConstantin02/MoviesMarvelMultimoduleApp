package com.example.data_repository.episode

import arrow.core.left
import arrow.core.right
import com.example.data_mapper.DtoToEpisodeBo.toEpisodesBo
import com.example.data_mapper.DtoToEpisodeEntityMapper.toEpisodesEntities
import com.example.data_mapper.EntityToEpisodeBoMapper.toEpisodesBo
import com.example.database.detasource.episode.IEpisodeLocalDataSource
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.remote.episode.datasource.IEpisodeRemoteDataSource
import com.example.resources.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val remote: IEpisodeRemoteDataSource,
    private val local: IEpisodeLocalDataSource
) : IEpisodeRepository {

    override fun getEpisodes(episodesIds: List<Int>): Flow<Result<List<EpisodeBo>>> = flow {
        local.getEpisodes(episodesIds).fold(
            ifLeft = {
                remote.getEpisodesByIds(episodesIds).fold(
                    ifLeft = {
                        it.left()
                    }
                ) { episodesResult ->
                    episodesResult?.filterNotNull()?.let { episodes ->
                        if (episodes.isNotEmpty()) {
                            local.insertEpisodes(episodes.toEpisodesEntities()).onRight {
                                emit(getLocalEpisodes(episodesIds).onLeft {
                                    emit(episodes.toEpisodesBo().right())
                                })
                            }.onLeft { emit(episodes.toEpisodesBo().right()) }
                        }
                    }
                }
            }
        ) {
                episodesEntity -> emit(episodesEntity.toEpisodesBo().right())
        }
    }

    private suspend fun getLocalEpisodes(episodesIds: List<Int>) =
        local.getEpisodes(episodesIds).fold(
            ifLeft = { it.left() }
        ) { episodesEntity ->
            episodesEntity.toEpisodesBo().right()
        }

}