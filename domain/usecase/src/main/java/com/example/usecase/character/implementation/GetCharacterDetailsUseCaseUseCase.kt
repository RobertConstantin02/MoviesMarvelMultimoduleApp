package com.example.usecase.character.implementation

import android.net.Uri
import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.characterDetail.CharacterWithLocation
import com.example.domain_model.episode.EpisodeBo
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.domain_repository.di.QEpisodesRepository
import com.example.domain_repository.di.QLocationRepository
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.domain_repository.location.ILocationRepository
import com.example.resources.Result
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.character.Params
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetCharacterDetailsUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val characterRepository: ICharacterRepository,
    @QLocationRepository private val locationRepository: ILocationRepository,
    @QEpisodesRepository private val episodesRepository: IEpisodeRepository,
) : IGetCharacterDetailsUseCase {
    override suspend fun run(input: Params): Flow<Result<CharacterPresentationScreenBO>> {


        return combine(
            characterRepository.getCharacter(input.characterId),
            locationRepository.getExtendedLocation(input.locationId)
        ) { characterResult, locationResult ->
            characterResult.fold(
                ifLeft = { it.left() },
            ) { character ->
                locationResult.fold(
                    ifLeft = { it.left() }
                ) { location ->
                    Log.d("-----> character", character.toString())
                    Log.d("-----> location", location.toString())
                    CharacterWithLocation(
                        Pair(character, character.episodes),
                        Pair(location, location.residents)
                    ).right()
                }
            }
        }.transform { characterWithLocation ->
            Log.d("-----> transform", "called")
            characterWithLocation.fold(
                ifLeft = {
                    emit(it.left())
                },
                ifRight = {
                    combineResidentsAndEpisodes(it)
                }
            )
        }
    }

    private suspend fun FlowCollector<Result<CharacterPresentationScreenBO>>.combineResidentsAndEpisodes(
        characterWithLocation: CharacterWithLocation
    ) {
        val (character, location) = characterWithLocation
        combine(
            characterRepository.getCharactersByIds(getIds(character.second)),
            episodesRepository.getEpisodes(getIds(location.second))
        ) { residentsResult, episodesResult ->
            Log.d("-----> combineResidentsAndEpisodes", "called")
            handleResidentEpisodeTransformation(
                characterWithLocation,
                residentsResult,
                episodesResult
            )
        }.collect()
    }

    private suspend fun FlowCollector<Result<CharacterPresentationScreenBO>>.handleResidentEpisodeTransformation(
        characterWithLocation: CharacterWithLocation,
        residentsResult: Either<Throwable, List<CharacterNeighborBo>>,
        episodesResult: Either<Throwable, List<EpisodeBo>>
    ) {
        residentsResult.fold(
            ifLeft = { emit(it.left()) }
        ) { residents ->
            episodesResult.fold(
                ifLeft = { emit(it.left()) }
            ) { episodes ->
                Log.d("-----> handleResidentEpisodeTransformation", "called")
                emit(
                    CharacterPresentationScreenBO(
                        characterWithLocation.characterMainDetail.first,
                        characterWithLocation.extendedLocation.first,
                        residents,
                        episodes
                    ).right()
                )
            }
        }
    }

    private fun getIds(urls: List<String?>?) =
        urls?.mapNotNull {
            Uri.parse(it).lastPathSegment?.toInt()
        } ?: emptyList()

}
