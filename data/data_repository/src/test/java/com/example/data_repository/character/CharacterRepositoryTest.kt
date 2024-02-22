package com.example.data_repository.character

import androidx.paging.PagingSource
import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.support.expected
import assertk.assertions.support.show
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.LocalUnifiedError
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiUnifiedError
import com.example.data_mapper.DtoToCharacterDetailBoMapper.toCharacterDetailBo
import com.example.data_mapper.DtoToCharacterEntityMapper.toCharacterEntity
import com.example.data_mapper.EntityToCharacterBoMapper.toCharacterBo
import com.example.data_mapper.toCharacterNeighborBo
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainLocalUnifiedError
import com.example.domain_model.resource.DomainResource
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.test.character.CharacterUtil
import com.example.database.detasource.character.fake.CharacterLocalDataSourceFake
import com.example.remote.character.datasource.fake.CharacterRemoteDataSourceFake
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection

// TODO: make shared module for testing and for implementation where interfaces are placed
/**
 * I can create a new shared module and move your common interfaces and classes which are used
 * in both application code and test code from main to this shared module. And then you can use
 * implementation project(':shared') in application code and testImplementation project(':shared') in test classes to include them.
 *
 * For example, if you have interfaces like DataSource implemented in both production code
 * (like RealDataSource: DataSource) and test code (like FakeDataSource: DataSource), DataSource
 * should be defined in the shared module's main sourceset, RealDataSource should be in your main
 * sourceset, and FakeDataSource should be in your test sourceset.
 */

const val CHARACTER_ID = 2
const val OFFSET = 10
const val TEST_ERROR_MESSAGE = "Test Error"

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryTest {
    private lateinit var remoteDataSource: ICharacterRemoteDataSource
    private lateinit var localDatasource: ICharacterLocalDatasource
    private lateinit var repository: CharacterRepository
    private lateinit var sharedPref: ISharedPreferenceDataSource

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        remoteDataSource = CharacterRemoteDataSourceFake()
        localDatasource = CharacterLocalDataSourceFake()
        sharedPref = mockk(relaxed = true)
        repository = CharacterRepository(remoteDataSource, localDatasource, sharedPref)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAllCharacters, PagingSource load success`() = runTest {
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()
        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)

        val expected = PagingSource.LoadResult.Page(
            data = fakeLocalData.take(10),
            prevKey = 0,
            nextKey = 2
        ).data

        val result = ((localDatasource as CharacterLocalDataSourceFake).getAllCharacters().load(
            PagingSource.LoadParams.Append(1, 10, false)
        ) as? PagingSource.LoadResult.Page)?.data

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getAllCharacters, PagingSource load failure`() = runTest {
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()

        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        (localDatasource as CharacterLocalDataSourceFake).paginationError = true

        val expected = PagingSource.LoadResult.Error<Int, CharacterEntity>(
            Exception(
                "pagination test error",
                Throwable()
            )
        ).throwable.message

        val result = ((localDatasource as CharacterLocalDataSourceFake).getAllCharacters().load(
            PagingSource.LoadParams.Append(1, 10, false)
        ) as? PagingSource.LoadResult.Error)?.throwable?.message

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `getCharactersByIds call, returns Resource Success when api success and local success`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when api success and local error`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(LocalUnifiedError.Reading)
            (localDatasource as CharacterLocalDataSourceFake).insertError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when api success and local empty`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).databaseEmpty =
                DatabaseResponseEmpty()
            (localDatasource as CharacterLocalDataSourceFake).insertError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when database has not saved api data`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (localDatasource as CharacterLocalDataSourceFake).insertError =
                DatabaseResponseError(LocalUnifiedError.Insertion)
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with local data when api error and local success`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()

            val fakeLocalData =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(ApiUnifiedError.Generic(TEST_ERROR_MESSAGE))

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns 0L
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                val apiErrorMessage =
                    (result.domainState as? DomainResource.DomainState.Error)?.error
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
                assertThat(apiErrorMessage).isEqualTo(DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE))
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with message and null data when api error and local error`() =
        runTest {
            //Given
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(ApiUnifiedError.Generic("Generic Error"))
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(LocalUnifiedError.Reading)
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                val apiErrorMessage =
                    (result.domainState as? DomainResource.DomainState.Error)?.error
                //Then
                assertThat(apiErrorMessage).isEqualTo(DomainApiUnifiedError.Generic(TEST_ERROR_MESSAGE))
                assertThat(result.domainState.unwrap()).isNull()
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with message and null data when api error and local empty`() =
        runTest {
            //Then
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.SuccessEmpty::class.java)
                assertThat(result.domainState.unwrap()).isNull()
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success with localData when local success`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()

            val fakeLocalData =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()
            every { sharedPref.getTime() } returns System.currentTimeMillis()
            //When
            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)


            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local success`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Success(
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local error`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Success(
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(LocalUnifiedError.Reading)
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local empty`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Success(
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).databaseEmpty =
                DatabaseResponseEmpty()
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when database has not saved api data`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Success(
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).insertError =
                DatabaseResponseError(LocalUnifiedError.Insertion)
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with local data when api error and local success`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Error(
                    DomainApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    ),
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character -> character.id == CHARACTER_ID }
                )


            val fakeLocalData =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(
                    ApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    )
                )

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns 0L

            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiError =
                    (result.domainState as? DomainResource.DomainState.Error)?.error
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Error::class.java)
                assertThat(apiError).isEqualTo( DomainApiUnifiedError.Http.Unauthorized(
                    TEST_ERROR_MESSAGE,
                    HttpURLConnection.HTTP_UNAUTHORIZED
                ))
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with message and null data when api error and local error`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Error(
                    DomainApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    ), null
                )

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(
                    ApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    )
                )
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(LocalUnifiedError.Reading)

            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiErrorMessage =
                    (result.domainState as? DomainResource.DomainState.Error)?.error
                //Then
                assertThat(result.domainState.unwrap()).isNull()
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Error::class.java)
                assertThat(result.domainState).isEqualTo(expected)
                assertThat(apiErrorMessage).isEqualTo(expected.error)
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with message and null data when api error and local empty`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Error(
                    DomainApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    ), null
                )
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(
                    ApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    )
                )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiErrorMessage =
                    (result.domainState as? DomainResource.DomainState.Error)?.error
                //Then
                assertThat(result.domainState.unwrap()).isNull()
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Error::class.java)
                assertThat(result.domainState).isEqualTo(expected)
                assertThat(apiErrorMessage).isEqualTo(
                    DomainApiUnifiedError.Http.Unauthorized(
                        TEST_ERROR_MESSAGE,
                        HttpURLConnection.HTTP_UNAUTHORIZED
                    )
                )
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when database has saved data`() =
        runTest {
            //Given
            val expected =
                DomainResource.DomainState.Success(
                    CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })
            val fakeLocalData =
                CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns System.currentTimeMillis()
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.domainState.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Success::class.java)
            }
        }

    @Test
    fun `updateCharactersIsFavorite, update is success`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()
        //When
        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        //Then
        repository.updateCharacterIsFavorite(true, 2).collectLatest { result ->
            assertThat(result.domainState).isEqualTo(DomainResource.DomainState.Success(Unit))
        }
    }

    @Test
    fun `updateCharactersIsFavorite, update is error`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()

        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        (localDatasource as CharacterLocalDataSourceFake).updateError =
            DatabaseResponseError(LocalUnifiedError.Update)
        //When
        repository.updateCharacterIsFavorite(true, 2).collectLatest { result ->
            //Then
            assertThat(result.domainState).isEqualTo(
                DomainResource.DomainState.Error(DomainLocalUnifiedError.Update, null)
            )
        }
    }

    @Test
    fun `getFavoriteCharacters, returns local success`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.map {
                it.toCharacterEntity().copy(isFavorite = true)
            } ?: emptyList()

        val expected =
            CharacterUtil.expectedSuccessCharactersFirstPage.results?.filterNotNull()?.subList(0, 10)?.map {
                it.toCharacterEntity().copy(isFavorite = true).toCharacterBo()
            } ?: emptyList()
        //When
        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        //Then
        repository.getFavoriteCharacters(0, OFFSET).collectLatest { result ->
            assertThat(result.domainState.unwrap().orEmpty()).isExpectedCharacters(expected)
        }
    }

    @Test
    fun `getFavoriteCharacters, returns local error`() = runTest {
        //Given
        (localDatasource as CharacterLocalDataSourceFake).readError =
            DatabaseResponseError(LocalUnifiedError.Reading)
        //When
        repository.getFavoriteCharacters(1, OFFSET).collectLatest { result ->
            //Then
            assertThat(result.domainState.unwrap()).isNull()
            assertThat(result.domainState).isInstanceOf(DomainResource.DomainState.Error::class.java)
        }
    }


    private fun Assert<CharacterDetailBo?>.isExpectedCharacter(expectedId: Int?) =
        given { actual ->
            if (expectedId == actual?.id) return
            expected("character id: ${show(expectedId)} but was character id: ${show(actual?.id)}")
        }

    private fun Assert<List<CharacterNeighborBo>>.isExpectedNeighbors(
        expected: List<CharacterNeighborBo>
    ) = given { actual ->
        if (expected.size == actual.size && expected.zip(actual).all { (actual, expected) ->
                actual.image.value.orEmpty() == expected.image.value.orEmpty() &&
                        actual.id == expected.id
            }) return

        expected("characters: ${show(expected)} but was characters: ${show(actual)}")
    }

    private fun Assert<List<CharacterBo>>.isExpectedCharacters(
        expected: List<CharacterBo>
    ) = given { actual ->
        if (expected.size == actual.size && expected.zip(actual).all { (actual, expected) ->
                actual.id == expected.id
            }) return

        expected("character: ${show(expected)} but was character: ${show(actual)}")
    }
}