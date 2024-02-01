package com.example.data_repository.character

import assertk.Assert
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.support.expected
import assertk.assertions.support.show
import com.example.core.Resource
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseUnifiedError
import com.example.core.remote.ApiResponseError
import com.example.core.remote.UnifiedError
import com.example.data_mapper.DtoToCharacterEntityMapper.toCharacterEntity
import com.example.data_mapper.toCharacterNeighborBo
import com.example.data_repository.fake.CharacterLocalDataSourceFake
import com.example.data_repository.fake.CharacterRemoteDataSourceFake
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.domain_model.character.CharacterNeighborBo
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.test.character.CharacterUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CharacterRepositoryTest {
    private lateinit var remoteDataSource: ICharacterRemoteDataSource
    private lateinit var localDatasource: ICharacterLocalDatasource
    private lateinit var repository: CharacterRepository

    @BeforeEach
    fun setUp() {
        remoteDataSource = CharacterRemoteDataSourceFake()
        localDatasource = CharacterLocalDataSourceFake()
        repository = CharacterRepository(remoteDataSource, localDatasource)
    }

    /**
     * 1*
     * Local database does not have any data. Request to remote datasource is made.
     * @isEqualToWithGivenProperties -> only compares the fields, not class itself.
     */

    //now that I know that this works will colled try to break the inliune function and see
    @Test
    fun `getCharactersByIds call, returns Resource Success when requesting from remote and local DatabaseResponseSuccess`() =
        runTest {
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            //Given
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )

            //When
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                //assertThat(result.state).isInstanceOf(Resource.State.Success::class)
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    /**
     * *2
     */
    @Test
    fun `getCharactersByIds call, returns Resource Success when requesting from api success and DatabaseResponseError`() =
        runTest {
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

    /**
     * *3
     */
    @Test
    fun `getCharactersByIds call, returns Resource Success when requesting from api success and DatabaseResponseEmpty`() = runTest {
        val expected =
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                it.toCharacterNeighborBo()
            } ?: emptyList()
        (localDatasource as CharacterLocalDataSourceFake).databaseEmpty = DatabaseResponseEmpty()
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

    /**
     * *4
     */
    @Test
    fun `getCharactersByIds call, returns Resource Success if database has not saved api data`() =
        runTest {
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (localDatasource as CharacterLocalDataSourceFake).readError = null
            (localDatasource as CharacterLocalDataSourceFake).insertError = DatabaseResponseError(DatabaseUnifiedError.Insertion)
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
            (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
            )
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                //Then
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
            }
        }

    /**
     * *5
     */
    @Test
    fun `getCharactersByIds call, returns Resource Error with local data if fetch from local is DatabaseResponseSuccess`() =
        runTest {
            val expected =
                CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
                    it.toCharacterNeighborBo()
                } ?: emptyList()
            (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = ApiResponseError(UnifiedError.Generic("Generic error"))
            repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
                val apiErrorMessage = (result.state as? Resource.State.Error)?.apiError
                //Then
                //assert message also if is iqual to expected
                assertThat(result.state.unwrap().orEmpty()).isExpectedNeighbors(expected)
                assertThat(apiErrorMessage).isEqualTo(UnifiedError.Generic("Generic error").message)
            }
        }

    /**
     * *6
     * Simulate:
     * Api error
     * Local database with data
     *
     *
     */
    @Test
    fun `getCharactersByIds call, returns Resource Error with api error message and null data`() = runTest {
        val expectedError = UnifiedError.Generic("Generic Error")
        (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = ApiResponseError(UnifiedError.Generic("Generic error"))
        (localDatasource as CharacterLocalDataSourceFake).readError =
            DatabaseResponseError(DatabaseUnifiedError.Reading)
//        (localDatasource as CharacterLocalDataSourceFake).setCharacters(
//            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3)?.map {
//                it.toCharacterEntity()
//            } ?: emptyList()
//        )
        val result = repository.getCharactersByIds(listOf(1, 2, 3)).drop(1).first()
        println("-----> apiError: ${(result?.state as? Resource.State.Error)?.apiError}")
        val apiErrorMessage = (result?.state as? Resource.State.Error)?.apiError
        assertThat(apiErrorMessage).isEqualTo(expectedError.message)
        assertThat(result?.state?.unwrap()).isNull()
    }


    private fun Assert<List<CharacterNeighborBo>>.isExpectedNeighbors(
        expected: List<CharacterNeighborBo>
    ) = given { actual ->
        if (expected.size == actual.size && expected.zip(actual).all { (actual, expected) ->
                actual.image.value.orEmpty() == expected.image.value.orEmpty() &&
                        actual.id == expected.id
            }) return

        expected("character: ${show(expected)} but was character: ${show(actual)}")
    }
}