package com.example.data_repository.character

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.core.Resource
import com.example.data_repository.fake.CharacterLocalDataSourceFake
import com.example.data_repository.fake.CharacterRemoteDataSourceFake
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.test.character.CharacterUtil
import kotlinx.coroutines.flow.collectLatest
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
     * Local database does not have any data. Request to remote datasource is made.
     */
    @Test
    fun `getCharactersByIds call, returns Resource Success when requesting from remote`() = runTest {
        val expected = CharacterUtil.expectedSuccessCharacters.results?.filterNotNull()?.take(3) ?: emptyList()
        //Given
        (localDatasource as CharacterLocalDataSourceFake).localError = null
        (remoteDataSource as CharacterRemoteDataSourceFake).remoteError = null
        (remoteDataSource as CharacterRemoteDataSourceFake).setCharacters(
            CharacterUtil.expectedSuccessCharacters.results?.filterNotNull() ?: listOf()
        )
        //When
        repository.getCharactersByIds(listOf(1, 2, 3)).collectLatest { result ->
            //Then
            assertThat(result).isEqualTo(Resource.State.Success(expected))
        }
    }
}