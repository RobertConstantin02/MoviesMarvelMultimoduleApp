package com.example.database

import android.content.Context
import android.os.Build.VERSION_CODES.Q
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.DifferCallback
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.dao.character.ICharacterDao
import com.example.database.entities.CharacterEntity
import com.example.database.util.CharacterEntityUtil
import com.example.database.util.PagingSourceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
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
    private val testDispatcher = TestCoroutineDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RickMortyDatabase::class.java).allowMainThreadQueries().build()
        characterDao = db.characterDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun closeDb() {
        Dispatchers.resetMain()
        db.close()
    }

//    @Test
//    fun `getAllCharacters, returns load data properly`() = runTest {
//        val pagingSource = PagingSourceUtils(CharacterEntityUtil.expectedCharactersEntity).load(
//            PagingSource.LoadParams.Append(
//                1, 10, false
//            )
//        )
//        val result = (pagingSource as? PagingSource.LoadResult.Page)?.data
//        assertEquals(CharacterEntityUtil.expectedCharactersEntity.take(10), result)
//    }

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
        val expected = CharacterEntityUtil.createCharacter(3)
        characterDao.insertCharacter(expected)
        val result = characterDao.getCharacterById(3)
        println("-----> $result")
        assertEquals(expected, result)
    }

    @Test
    @Throws(Exception::class)
    fun `write and read characters by id`() {

    }

    private fun <PaginationKey: Any, Model: Any>PagingSource<PaginationKey, Model>.getData(): List<Model> {
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