package com.example.database.detasource.character

import com.example.database.dao.IPagingKeysDao
import com.example.database.dao.character.ICharacterDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.database.util.CharacterEntityUtil
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test

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
            remoteKeys.add(PagingKeys(it.toLong(), null, "https://rickandmortyapi.com/api/character/?page=2"))
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllCharacters returns PagingSource with proper data`() {

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
}