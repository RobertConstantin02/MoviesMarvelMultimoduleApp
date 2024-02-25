package com.example.usecase.character.implementation

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_repository.character.ICharacterRepository
import com.example.domain_repository.episode.IEpisodeRepository
import com.example.domain_repository.location.ILocationRepository
import com.example.usecase.character.IGetCharacterDetailsUseCase
import com.example.usecase.character.fake.CharacterRepositoryFake
import com.example.usecase.character.fake.EpisodeRepositoryFake
import com.example.usecase.character.fake.LocationRepositoryFake
import com.example.usecase.util.DomainUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterEach

const val CHARACTER_ID = 3
const val LOCATION_ID = 2

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetCharacterDetailsUseCaseUseCaseTest {
    private lateinit var characterRepository: ICharacterRepository
    private lateinit var locationRepository: ILocationRepository
    private lateinit var episodesRepository: IEpisodeRepository
    private lateinit var getCharacterDetailsUseCaseUseCase: IGetCharacterDetailsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        characterRepository = CharacterRepositoryFake()
        locationRepository = LocationRepositoryFake()
        episodesRepository = EpisodeRepositoryFake()
        getCharacterDetailsUseCaseUseCase = GetCharacterDetailsUseCaseUseCase(
            characterRepository, locationRepository, episodesRepository
        )
        Dispatchers.setMain(testDispatcher)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @org.junit.Test
    fun `getCharactersDetailsUseCase, returns DomainResource Success`() = runTest(testDispatcher) {
        val characterDetailBo =
            DomainUtil.charactersDetail[2]

        val extendedLocationBo =
            DomainUtil.extendedLocation[1]

        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = CharacterPresentationScreenBO(
            characterDetailBo,
            extendedLocationBo,
            neighbors,
            episodes
        )
        //Given
        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(DomainUtil.charactersDetail.toList())
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).setExtendedLocation(DomainUtil.extendedLocation.toList())
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        getCharacterDetailsUseCaseUseCase.run(
            IGetCharacterDetailsUseCase.Params(CHARACTER_ID, LOCATION_ID)
        ).take(1).collectLatest { result ->
            assertThat((result.domainState.unwrap())).isEqualTo(expected)
        }
    }
}