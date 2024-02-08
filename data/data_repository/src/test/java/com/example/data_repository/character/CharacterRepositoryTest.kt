package com.example.data_repository.character

import androidx.paging.PagingSource
import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.support.expected
import assertk.assertions.support.show
import com.example.core.Resource
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseUnifiedError
import com.example.core.remote.ApiResponseError
import com.example.core.remote.UnifiedError
import com.example.data_mapper.DtoToCharacterDetailBoMapper.toCharacterDetailBo
import com.example.data_mapper.DtoToCharacterEntityMapper.toCharacterEntity
import com.example.data_mapper.EntityToCharacterBoMapper.toCharacterBo
import com.example.data_mapper.toCharacterNeighborBo
import com.example.data_repository.fake.CharacterLocalDataSourceFake
import com.example.data_repository.fake.CharacterRemoteDataSourceFake
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import com.example.domain_model.character.CharacterBo
import com.example.domain_model.character.CharacterNeighborBo
import com.example.domain_model.characterDetail.CharacterDetailBo
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.test.character.CharacterUtil
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

const val CHARACTER_ID = 2
const val OFFSET = 10

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
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.map {
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
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.map {
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
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when api success and local error`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(DatabaseUnifiedError.Reading)
            (localDatasource as CharacterLocalDataSourceFake).insertError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when api success and local empty`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).databaseEmpty =
                DatabaseResponseEmpty()
            (localDatasource as CharacterLocalDataSourceFake).insertError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success when database has not saved api data`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (localDatasource as CharacterLocalDataSourceFake).insertError =
                DatabaseResponseError(DatabaseUnifiedError.Insertion)
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with local data if local is success`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()

            val fakeLocalData =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(UnifiedError.Generic("Generic error"))

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns 0L
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
                assertThat(apiErrorMessage).isEqualTo(UnifiedError.Generic("Generic error").message)
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with message and null data when api error and local error`() =
        runTest {
            //Given
            val expectedError = UnifiedError.Generic("Generic Error")
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(UnifiedError.Generic("Generic Error"))
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(DatabaseUnifiedError.Reading)
            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                assertThat(apiErrorMessage).isEqualTo(expectedError.message)
                assertThat(result.state.unwrap()).isNull()
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Error with message and null data when api error and local empty`() =
        runTest {
            //Then
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                assertThat(result.state).isInstanceOf(Resource.State.SuccessEmpty::class.java)
                assertThat(result.state.unwrap()).isNull()
            }
        }

    @Test
    fun `getCharactersByIds call, returns Resource Success with localData when local success`() =
        runTest {
            //Given
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()

            val fakeLocalData =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()
            every { sharedPref.getTime() } returns System.currentTimeMillis()
            //When
            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)


            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local success`() =
        runTest {
            //Given
            val expected =
                Resource.State.Success(
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local error`() =
        runTest {
            //Given
            val expected =
                Resource.State.Success(
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(DatabaseUnifiedError.Reading)
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when api success and local empty`() =
        runTest {
            //Given
            val expected =
                Resource.State.Success(
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).databaseEmpty =
                DatabaseResponseEmpty()
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when database has not saved api data`() =
        runTest {
            //Given
            val expected =
                Resource.State.Success(
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })

            (localDatasource as CharacterLocalDataSourceFake).insertError =
                DatabaseResponseError(DatabaseUnifiedError.Insertion)
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Success::class.java)
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with local data when api error and local success`() =
        runTest {
            //Given
            val expected =
                Resource.State.Error(
                    "Generic error",
                    null,
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    }
                )


            val fakeLocalData =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(UnifiedError.Generic("Generic error"))

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns 0L

            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Error::class.java)
                assertThat(apiErrorMessage).isEqualTo("Generic error")
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with message and null data when api error and local success`() =
        runTest {
            //Given
            val expectedErrorState =
                Resource.State.Error("Generic error", null, null)

            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError =
                ApiResponseError(UnifiedError.Generic("Generic Error"))
            (localDatasource as CharacterLocalDataSourceFake).readError =
                DatabaseResponseError(DatabaseUnifiedError.Reading)

            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                assertThat(result.state.unwrap()).isNull()
                assertThat(result.state).isInstanceOf(Resource.State.Error::class.java)
                assertThat(result.state).isEqualTo(expectedErrorState)
                assertThat(apiErrorMessage).isEqualTo(expectedErrorState.apiError)
            }
        }

    @Test
    fun `getCharacter call, returns Resource error with message and null data when api error and local empty`() =
        runTest {
            //Given
            val expectedErrorState = Resource.State.SuccessEmpty
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                assertThat(result.state.unwrap()).isNull()
                assertThat(result.state).isInstanceOf(Resource.State.SuccessEmpty::class.java)
                assertThat(result.state).isEqualTo(expectedErrorState)
                assertThat(apiErrorMessage).isNull()
            }
        }

    @Test
    fun `getCharacter call, returns Resource Success when database has saved data`() =
        runTest {
            //Given
            val expected =
                Resource.State.Success(
                    CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                        it.toCharacterDetailBo()
                    }?.first { character ->
                        character.id == CHARACTER_ID
                    })
            val fakeLocalData =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterEntity()
                } ?: emptyList()

            (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
            every { sharedPref.getTime() } returns System.currentTimeMillis()
            //When
            repository.getCharacter(CHARACTER_ID).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap()).isExpectedCharacter(expected.data?.id)
                assertThat(result.state).isInstanceOf(Resource.State.Success::class.java)
            }
        }

    @Test
    fun `updateCharactersIsFavorite, update is success`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()
        //When
        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        //Then
        repository.updateCharacterIsFavorite(true, 2).collectLatest { result ->
            assertThat(result.state).isEqualTo(Resource.State.Success(Unit))
        }
    }

    @Test
    fun `updateCharactersIsFavorite, update is error`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.map {
                it.toCharacterEntity()
            } ?: emptyList()

        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        (localDatasource as CharacterLocalDataSourceFake).updateError =
            DatabaseResponseError(DatabaseUnifiedError.Update)
        //When
        repository.updateCharacterIsFavorite(true, 2).collectLatest { result ->
            //Then
            assertThat(result.state).isEqualTo(
                Resource.State.Error(
                    null,
                    DatabaseUnifiedError.Update.messageResource,
                    null
                )
            )
        }
    }

    @Test
    fun `getFavoriteCharacters, returns local success`() = runTest {
        //Given
        val fakeLocalData =
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.map {
                it.toCharacterEntity().copy(isFavorite = true)
            } ?: emptyList()

        val expected =
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.subList(0, 10)?.map {
                it.toCharacterEntity().copy(isFavorite = true).toCharacterBo()
            } ?: emptyList()
        //When
        (localDatasource as CharacterLocalDataSourceFake).setCharacters(fakeLocalData)
        //Then
        repository.getFavoriteCharacters(0, OFFSET).collectLatest { result ->
            assertThat(result.state.unwrap().orEmpty()).isExpectedCharacters(expected)
        }
    }

    @Test
    fun `getFavoriteCharacters, returns local error`() = runTest {
        //Given
        (localDatasource as CharacterLocalDataSourceFake).readError =
            DatabaseResponseError(DatabaseUnifiedError.Reading)
        //When
        repository.getFavoriteCharacters(1, OFFSET).collectLatest { result ->
            //Then
            assertThat(result.state.unwrap()).isNull()
            assertThat(result.state).isInstanceOf(Resource.State.Error::class.java)
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
        println("-----> actual size: ${actual.size}")
        println("-----> expected size: ${expected.size}")
        if (expected.size == actual.size && expected.zip(actual).all { (actual, expected) ->
                actual.id == expected.id
            }) return

        expected("character: ${show(expected)} but was character: ${show(actual)}")
    }
}