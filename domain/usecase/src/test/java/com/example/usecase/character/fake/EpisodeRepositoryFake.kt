package com.example.usecase.character.fake

import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_model.error.DomainUnifiedError
import com.example.domain_model.location.ExtendedLocationBo
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.episode.IEpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class EpisodeRepositoryFake: IEpisodeRepository {
    private val episodes = MutableStateFlow<List<EpisodeBo>?>(emptyList())

    var error: DomainUnifiedError? = null
    var databaseEmpty: DomainResource<Nothing>? = null

    fun setCharactersDetailBo(characters: List<EpisodeBo>?) {
        this.episodes.value = characters
    }
    override fun getEpisodes(episodesIds: List<Int>): Flow<DomainResource<List<EpisodeBo>>> {
        if (error != null) return flowOf(DomainResource.error(error!!, episodes.value?.filter { location -> location.id in episodesIds }))
        if (databaseEmpty != null) return flowOf(DomainResource.successEmpty()) //skippable?

        return episodes.map { locations ->
            locations?.filter { location ->
                location.id in episodesIds
            }?.let {
                if ()
                DomainResource.success(it)
            }
                ?: DomainResource.successEmpty()
        }
    }
}