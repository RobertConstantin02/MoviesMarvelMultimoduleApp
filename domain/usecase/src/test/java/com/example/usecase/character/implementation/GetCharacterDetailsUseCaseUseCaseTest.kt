package com.example.usecase.character.implementation

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.resource.DomainResource
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

const val CHARACTER_ID = 3
const val INVALID_CHARACTER_ID = -3
const val LOCATION_ID = 2
const val INVALID_LOCATION_ID = -2
const val TEST_ERROR_MESSAGE = "Error Test"

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
            characterRepository, locationRepository, episodesRepository, testDispatcher
        )
        Dispatchers.setMain(testDispatcher)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `combineResources success_success && success_success, run returns DomainResource Success`() =
        runTest(testDispatcher) {
            //Given
            val characterDetailBo = DomainUtil.charactersDetail.toList()
            val extendedLocationBo = DomainUtil.extendedLocation.toList()

            val neighbors = DomainUtil.neighbors.toList()
            val episodes = DomainUtil.episodes.toList()

            val expected = DomainResource.DomainState.Success(
                CharacterPresentationScreenBO(
                    characterDetailBo[2],
                    extendedLocationBo[1],
                    neighbors,
                    episodes
                )
            )
            (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
            (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

            (locationRepository as LocationRepositoryFake).setExtendedLocation(extendedLocationBo)
            (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

            assert(expected)
        }

    @Test
    fun `combineResources success_error && error, run returns DomainResource Error`() = runTest {
        //Given
        val characterDetailBo = DomainUtil.charactersDetail.toList()
        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Error(
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            CharacterPresentationScreenBO(
                characterDetailBo[2],
                null,
                null,
                episodes
            )
        )

        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).error =
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE)
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected)
    }

    @Test
    fun `combineResources success_empty & error, run returns DomainResource Error`() = runTest {
        //Given
        val characterDetailBo = DomainUtil.charactersDetail.toList()
        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Error(
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            CharacterPresentationScreenBO(
                null,
                null,
                null,
                null
            )
        )

        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).empty =
            DomainResource.DomainState.SuccessEmpty
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected)
    }

    @Test
    fun `combineResources error && error, run returns DomainResource Error`() = runTest {
        //Given
        val extendedLocationBo = DomainUtil.extendedLocation.toList()
        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Error(
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            CharacterPresentationScreenBO(
                null,
                extendedLocationBo[1],
                neighbors,
                null
            )
        )

        (characterRepository as CharacterRepositoryFake).error =
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).setExtendedLocation(extendedLocationBo)
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected)
    }

    @Test
    fun `invalid characterId and valid locationId, combineResources error && success, run returns DomainResource Error`() = runTest {
        //Given
        val characterDetailBo = DomainUtil.charactersDetail.toList()
        val extendedLocationBo = DomainUtil.extendedLocation.toList()
        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Error(
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            CharacterPresentationScreenBO(
                null,
                extendedLocationBo[1],
                neighbors,
                null
            )
        )

        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).setExtendedLocation(extendedLocationBo)
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected, INVALID_CHARACTER_ID)
    }

    @Test
    fun `invalid characterId and invalid locationId, combineResources error && success, run returns DomainResource Error`() = runTest {
        //Given
        val characterDetailBo = DomainUtil.charactersDetail.toList()
        val extendedLocationBo = DomainUtil.extendedLocation.toList()
        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Error(
            DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            CharacterPresentationScreenBO(
                null,
                null,
                null,
                null
            )
        )

        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).setExtendedLocation(extendedLocationBo)
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected, INVALID_CHARACTER_ID, INVALID_LOCATION_ID)
    }

    @Test
    fun `getIds handle null Ids properly`() = runTest {
        //Given
        val characterDetailBo = DomainUtil.charactersDetail.toList()
        val extendedLocationBo = DomainUtil.extendedLocationNullResidents.toList()

        val neighbors = DomainUtil.neighbors.toList()
        val episodes = DomainUtil.episodes.toList()

        val expected = DomainResource.DomainState.Success(
            CharacterPresentationScreenBO(
                characterDetailBo[2],
                extendedLocationBo[1],
                neighbors.filter { neighbor -> neighbor.id in listOf(2, 4) },
                episodes
            )
        )

        (characterRepository as CharacterRepositoryFake).setCharactersDetailBo(characterDetailBo)
        (characterRepository as CharacterRepositoryFake).setCharactersNeighborBo(neighbors)

        (locationRepository as LocationRepositoryFake).setExtendedLocation(extendedLocationBo)
        (episodesRepository as EpisodeRepositoryFake).setCharactersDetailBo(episodes)

        assert(expected)
    }

    private suspend fun <T> assert(
        expected: DomainResource.DomainState<T>,
        characterId: Int = CHARACTER_ID,
        locationId: Int = LOCATION_ID
    ) {
        //When
        getCharacterDetailsUseCaseUseCase.run(
            IGetCharacterDetailsUseCase.Params(characterId, locationId)
        ).take(1).collectLatest { result ->
            //Then
            assertThat((result.domainState.unwrap())).isEqualTo(expected.unwrap())
            assertThat(result.domainState).isInstanceOf(expected::class.java)
        }
    }
}