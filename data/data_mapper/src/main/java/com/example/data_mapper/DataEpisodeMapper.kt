package com.example.data_mapper

import com.example.api.model.episode.EpisodeDto
import com.example.database.entities.EpisodeEntity
import com.example.domain_model.episode.EpisodeBo

object DtoToEpisodeEntityMapper {
    fun List<EpisodeDto>.toEpisodesEntities() = map { episodeDto ->
        with(episodeDto) { EpisodeEntity(id, name, episode, date) }
    }
}

object EntityToEpisodeBoMapper {
    fun List<EpisodeEntity>.toEpisodesBo() = map { episodeEntity ->
        with(episodeEntity) { EpisodeBo(id, name, episode, date) }
    }
}

object DtoToEpisodeBo {
    fun List<EpisodeDto>.toEpisodesBo() = map { episodeDto ->
        with(episodeDto) {
            EpisodeBo(id, name, episode, date)
        }
    }
}