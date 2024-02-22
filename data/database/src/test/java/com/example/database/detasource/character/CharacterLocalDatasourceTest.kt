package com.example.database.detasource.character

import android.database.sqlite.SQLiteException
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.local.LocalUnifiedError
import com.example.database.dao.IPagingKeysDao
import com.example.database.dao.character.ICharacterDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.database.util.CharacterEntityUtil
import com.example.database.util.PagingSourceUtils
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue


const val VALID_CHARACTER_ID = 3
const val INVALID_CHARACTER_ID = -1
val VALID_CHARACTER_IDS = listOf(1, 2, 3)
val INVALID_CHARACTER_IDS = listOf(-1, -2, -3)

internal class CharacterLocalDatasourceTest {
    private lateinit var characterDao: ICharacterDao
    private lateinit var pagingKeysDao: IPagingKeysDao
    private lateinit var characterLocalDatasource: ICharacterLocalDatasource

    private val localCharacters = mutableListOf<CharacterEntity>()
    private val remoteKeys = mutableListOf<PagingKeys>()

    @Before
    fun setUp() {
        characterDao = mockk()
        pagingKeysDao = mockk()
        characterLocalDatasource = CharacterLocalDatasource(characterDao, pagingKeysDao)
        localCharacters.addAll(CharacterEntityUtil.expectedCharactersEntity)
        repeat(10) {
            remoteKeys.add(
                PagingKeys(
                    it.toLong(),
                    null,
                    "https://rickandmortyapi.com/api/character/?page=2"
                )
            )
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllCharacters returns PagingSource with proper data`() = runTest {
        //Given
        val expected = PagingSourceUtils(CharacterEntityUtil.expectedCharactersEntity)
        coEvery {
            characterDao.getAllCharacters()
        } returns expected
        //When
        val result = characterLocalDatasource.getAllCharacters()
        //Then
        assertEquals(expected, result)
    }

    @Test
    fun `getCharacterById returns DatabaseResponseSuccess when valid id`() = runTest {
        val expected = CharacterEntityUtil.createCharacter(VALID_CHARACTER_ID)

        coEvery {
            characterDao.getCharacterById(match { id -> id > 0 })
        } returns expected

        characterLocalDatasource.getCharacterById(expected.id).collectLatest { databaseResponse ->
            val result = (databaseResponse as? DatabaseResponseSuccess)?.data
            assertEquals(expected, result)
        }
    }

    @Test
    fun `getCharacterById returns DatabaseResponseEmpty when invalid id`() = runTest {
        val expected = DatabaseResponseEmpty<CharacterEntity>()
        coEvery {
            characterDao.getCharacterById(any())
        } returns null

        characterLocalDatasource.getCharacterById(INVALID_CHARACTER_ID).collectLatest { result ->
            assertThat(result).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `getCharacterById returns read error when sqlException`() = runTest {
        val expected = DatabaseResponseError<CharacterEntity>(LocalUnifiedError.Reading)
        coEvery {
            characterDao.getCharacterById(any())
        } throws SQLiteException()

        characterLocalDatasource.getCharacterById(VALID_CHARACTER_ID).collectLatest { result ->
            assertThat(
                (result as DatabaseResponseError).localUnifiedError
            ).isInstanceOf(expected.localUnifiedError::class.java)
        }
    }

    /**
     * ------------- getCharactersById
     */

    @Test
    fun `getCharactersById returns DatabaseResponseSuccess when valid ids`() = runTest {
        val expected = CharacterEntityUtil.createCharacters(10)

        coEvery {
            characterDao.getCharactersByIds(
                match { ids -> ids.all { it in VALID_CHARACTER_IDS } }
            )
        } returns expected

        characterLocalDatasource.getCharactersByIds(VALID_CHARACTER_IDS).collectLatest { databaseResponse ->
            val result = (databaseResponse as? DatabaseResponseSuccess)?.data
            assertEquals(expected, result)
        }
    }

    @Test
    fun `getCharactersById returns DatabaseResponseEmpty when list empty`() = runTest {
        val expected = DatabaseResponseEmpty<CharacterEntity>()

        coEvery {
            characterDao.getCharactersByIds(
                match { ids -> ids.all { it in INVALID_CHARACTER_IDS } }
            )
        } returns listOf()

        characterLocalDatasource.getCharactersByIds(INVALID_CHARACTER_IDS).collectLatest { result ->
            assertThat(result).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `getCharactersById returns DatabaseResponseEmpty when list null`() = runTest {
        val expected = DatabaseResponseEmpty<CharacterEntity>()

        coEvery {
            characterDao.getCharactersByIds(any())
        } returns null

        characterLocalDatasource.getCharacterById(INVALID_CHARACTER_ID).collectLatest { result ->
            assertThat(result).isInstanceOf(expected::class.java)
        }
    }

    @Test
    fun `getCharacterBysId returns DatabaseResponseError`() = runTest {
        val expected = DatabaseResponseError<CharacterEntity>(LocalUnifiedError.Reading)
        coEvery {
            characterDao.getCharacterById(any())
        } throws SQLiteException()

        characterLocalDatasource.getCharacterById(VALID_CHARACTER_ID).collectLatest { result ->
            assertThat(
                (result as DatabaseResponseError).localUnifiedError
            ).isInstanceOf(expected.localUnifiedError::class.java)
        }
    }
}