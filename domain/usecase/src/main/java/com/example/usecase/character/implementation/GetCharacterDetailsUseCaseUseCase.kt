package com.example.usecase.character.implementation

import android.net.Uri
import com.example.core.remote.Resource
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.characterDetail.CharacterWithLocation
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.domain_repository.di.QEpisodesRepository
import com.example.domain_repository.di.QLocationRepository
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.domain_repository.location.ILocationRepository
import com.example.usecase.character.IGetCharacterDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetCharacterDetailsUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val characterRepository: ICharacterRepository,
    @QLocationRepository private val locationRepository: ILocationRepository,
    @QEpisodesRepository private val episodesRepository: IEpisodeRepository,
) : IGetCharacterDetailsUseCase {

    override suspend fun run(input: IGetCharacterDetailsUseCase.Params): Flow<Resource<CharacterPresentationScreenBO>> {

        return combine(
            characterRepository.getCharacter(input.characterId),
            locationRepository.getExtendedLocation(input.locationId),
        ) { characterResult, locationResult ->
            characterResult.state.combineResources(locationResult.state) { character, location ->
                CharacterWithLocation(
                    Pair(character, character?.episodes),
                    Pair(location, location?.residents)
                )
            }
        }.transform {
            it.state.unwrap()?.let {
                combine(
                    characterRepository.getCharactersByIds(getIds(it.characterMainDetail.second)),
                    episodesRepository.getEpisodes(getIds(it.extendedLocation.second))
                ) { residentsResult, episodesResult ->
                    residentsResult.state.combineResources(episodesResult.state) { residents , episodes ->
                        CharacterPresentationScreenBO(
                            it.characterMainDetail.first,
                            it.extendedLocation.first,
                            residents,
                            episodes
                        )
                    }.let {
                        emit(it)
                    }
                }
            }
        }
//            .transform { characterWithLocationResource ->
//            characterWithLocationResource?.let {
//                with(it.unWrap()) {
//                    combine(
//                        characterRepository.getCharactersByIds(getIds(this?.characterMainDetail?.second)),
//                        episodesRepository.getEpisodes(getIds(this?.extendedLocation?.second))
//                    ) { residentsResult, episodesResult ->
//
//                        residentsResult.state.combineSuccess(episodesResult.state) { residents, episodes ->
//                            CharacterPresentationScreenBO(
//                                this?.characterMainDetail?.first,
//                                this?.extendedLocation?.first,
//                                residents,
//                                episodes
//                            )
//                        }?.let {
//                            emit(it)
//                        }
//
//                    }
//                }
//            }
//        }
//            .transform { characterWithLocation ->
//            characterWithLocation.fold(
//                ifLeft = { emit(it.left()) },
//                ifRight = { combineResidentsAndEpisodes(it) }
//            )
//        }
    }

//    private suspend fun FlowCollector<Result<CharacterPresentationScreenBO>>.combineResidentsAndEpisodes(
//        characterWithLocation: CharacterWithLocation
//    ) {
//        val (character, location) = characterWithLocation
//        combine(
//            characterRepository.getCharactersByIds(getIds(character.second)),
//            episodesRepository.getEpisodes(getIds(location.second))
//        ) { residentsResult, episodesResult ->
//            handleResidentEpisodeTransformation(
//                characterWithLocation,
//                residentsResult,
//                episodesResult
//            )
//        }.collect()
//    }
//
//    private suspend fun FlowCollector<Result<CharacterPresentationScreenBO>>.handleResidentEpisodeTransformation(
//        characterWithLocation: CharacterWithLocation,
//        residentsResult: Either<Throwable, List<CharacterNeighborBo>>,
//        episodesResult: Either<Throwable, List<EpisodeBo>>
//    ) {
//        residentsResult.fold(
//            ifLeft = { emit(it.left()) }
//        ) { residents ->
//            episodesResult.fold(
//                ifLeft = { emit(it.left()) }
//            ) { episodes ->
//                emit(
//                    CharacterPresentationScreenBO(
//                        characterWithLocation.characterMainDetail.first,
//                        characterWithLocation.extendedLocation.first,
//                        residents,
//                        episodes
//                    ).right()
//                )
//            }
//        }
//    }

    private fun getIds(urls: List<String?>?) =
        urls?.mapNotNull {
            Uri.parse(it).lastPathSegment?.toInt()
        } ?: emptyList()

}
