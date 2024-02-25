package com.example.database

import android.content.Context
import android.os.Build.VERSION_CODES.Q
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.dao.character.ICharacterDao
import com.example.database.util.CharacterEntityUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Q])
class RickMortyDatabaseTest {

    private lateinit var characterDao: ICharacterDao
    private lateinit var db: RickMortyDatabase
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RickMortyDatabase::class.java)
            .allowMainThreadQueries().build()
        characterDao = db.characterDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun closeDb() {
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun `write and read characters`() = runTest {
        //Given
        val expected = CharacterEntityUtil.createCharacters(10)
        //When
        characterDao.insertCharacters(*expected.toTypedArray())
        val result = characterDao.getAllCharacters().getData()
        println("$-----> $result")
        //Then
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun `write and read character`() = runTest {
        //Given
        val expected = CharacterEntityUtil.createCharacter(3)
        //When
        characterDao.insertCharacter(expected)
        val result = characterDao.getCharacterById(3)
        println("-----> $result")
        //Then
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun `write and read characters by id`() = runTest {
        //Given
        val expected = CharacterEntityUtil.createCharacters(10)
        //When
        characterDao.insertCharacters(*expected.toTypedArray())
        val result = characterDao.getCharactersByIds(listOf(1, 2, 3))
        println("-----> $result")
        //Then
        assertEquals(expected.filter { it.id in listOf(1, 2, 3) }, result)
    }

    @Test
    @Throws(Exception::class)
    fun `update character`() =  runTest {
        //Given
        val expected = CharacterEntityUtil.createCharacters(10)
        println("-----> $expected")
        val characterToUpdate = expected[(0..9).random()]
        println("-----> $characterToUpdate")
        //When
        characterDao.insertCharacters(*expected.toTypedArray())
        characterDao.updateCharacterIsFavorite(characterToUpdate.isFavorite.not(), characterToUpdate.id)
        val result = characterDao.getCharacterById(characterToUpdate.id)
        println("-----> $result")
        //Then
        assertNotEquals(characterToUpdate.isFavorite, result?.isFavorite)

    }

    @Test
    @Throws
    fun `read favorite characters`() = runTest {
        val fakeCharacters = CharacterEntityUtil.createCharacters(10)
        val expected = fakeCharacters.filter { it.isFavorite }
        println("-----> $expected")
        //When
        characterDao.insertCharacters(*expected.toTypedArray())
        val result = characterDao.getFavoriteCharacters(0, 10).first()
        println("-----> $result")
        assertEquals(expected, result)

    }

    private fun <PaginationKey : Any, Model : Any> PagingSource<PaginationKey, Model>.getData(): List<Model> {
        val data = mutableListOf<Model>()
        val latch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Main).launch {
            val loadResult: PagingSource.LoadResult<PaginationKey, Model> = this@getData.load(
                PagingSource.LoadParams.Refresh(
                    key = null, loadSize = 10, placeholdersEnabled = false
                )
            )
            when (loadResult) {
                is PagingSource.LoadResult.Error -> throw loadResult.throwable
                is PagingSource.LoadResult.Page -> data.addAll(loadResult.data)
                else -> {}
            }
            latch.countDown()
        }
        latch.await()
        job.cancel()
        return data
    }
}