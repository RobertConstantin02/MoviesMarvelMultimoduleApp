package com.example.usecase.character.implementation

import android.util.Log
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.characterDetail.CharacterWithLocation
import com.example.domain_model.resource.DomainResource
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.di.QCharacterRepository
import com.example.domain_repository.di.QEpisodesRepository
import com.example.domain_repository.di.QLocationRepository
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.domain_repository.location.ILocationRepository
import com.example.usecase.character.IGetCharacterDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import java.net.URI
import javax.inject.Inject

class GetCharacterDetailsUseCaseUseCase @Inject constructor(
    @QCharacterRepository private val characterRepository: ICharacterRepository,
    @QLocationRepository private val locationRepository: ILocationRepository,
    @QEpisodesRepository private val episodesRepository: IEpisodeRepository,
) : IGetCharacterDetailsUseCase {

    override suspend fun run(input: IGetCharacterDetailsUseCase.Params): Flow<DomainResource<CharacterPresentationScreenBO>> {
        return combine(
            characterRepository.getCharacter(input.characterId),
            locationRepository.getExtendedLocation(input.locationId),
        ) { characterResult, locationResult ->
            characterResult.domainState.combineResources(
                locationResult.domainState
            ) { character, location ->
//                println("-----> run 1 : $character || $location")
                CharacterWithLocation(
                    Pair(character, character?.episodes),
                    Pair(location, location?.residents)
                )
            }
        }.transform {
            val characterWithLocation = it.domainState.unwrap()
            combine(
                characterRepository.getCharactersByIds(
                    getIds(characterWithLocation?.extendedLocation?.second)
                ),
                episodesRepository.getEpisodes(
                    getIds(characterWithLocation?.characterMainDetail?.second)
                )
            ) { residentsResult, episodesResult ->
                emit(residentsResult.domainState.combineResources(
                    episodesResult.domainState
                ) { residents, episodes ->
                    println("-----> characterDetail: ${characterWithLocation?.characterMainDetail?.first}")
                    println("-----> characterLocation: ${characterWithLocation?.extendedLocation?.first}")
                    println("-----> characterLocation 2: ${characterWithLocation?.extendedLocation?.second}")
                    println("-----> residents: $residents")
                    println("-----> episodes: $episodes")
                    CharacterPresentationScreenBO(
                        characterWithLocation?.characterMainDetail?.first,
                        characterWithLocation?.extendedLocation?.first,
                        residents,
                        episodes
                    )
                })
            }.collect()
        }
    }

    private fun getIds(urls: List<String?>?) =
        urls?.mapNotNull { url ->
            url?.let { URI(it).path.split("/").last().toInt() }
        } ?: emptyList()
}
