package com.example.database

import android.content.Context
import android.os.Build
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.database.dao.character.ICharacterDao
import com.example.database.dao.util.CharacterEntityUtil
import com.example.database.dao.util.PagingSourceUtils
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class RickMortyDatabaseTest2 {
    private lateinit var characterDao: ICharacterDao
    private lateinit var db: RickMortyDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, RickMortyDatabase::class.java
        ).build()
        characterDao = db.characterDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @org.junit.Test
    fun `getAllCharacters as`() = runTest {
        val pagingSource = PagingSourceUtils(CharacterEntityUtil.expectedCharactersEntity).load(
            PagingSource.LoadParams.Append(
                1, 10, false
            )
        )
        val result = (pagingSource as? PagingSource.LoadResult.Page)?.data
        //assertk.assertThat(result).isEqualTo(CharacterEntityUtil.expectedCharactersEntity)
    }

}