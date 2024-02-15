package com.example.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiUnifiedError
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.database.detasource.character.fake.CharacterLocalDataSourceFake
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.remote.character.datasource.fake.CharacterRemoteDataSourceFake
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

const val API_PAGE_SIZE = 20
const val FAKE_PAGES = 3
const val FAKE_LOAD = 4
const val LOAD_SIZE = 10

const val TEST_ERROR_MESSAGE = "Test error message"
const val QUERY_PAGE = "https://rickandmortyapi.com/api/character/?page="
@OptIn(ExperimentalPagingApi::class)
internal class FeedRemoteMediatorTest {
    private lateinit var localDatasource: ICharacterLocalDatasource
    private lateinit var remoteDataSource: ICharacterRemoteDataSource
    private lateinit var feedRemoteMediator: FeedRemoteMediator

    @BeforeEach
    fun setUp() {
        localDatasource = CharacterLocalDataSourceFake()
        remoteDataSource = CharacterRemoteDataSourceFake()
        feedRemoteMediator = FeedRemoteMediator(localDatasource, remoteDataSource)
    }

    /**
     * When creating a PagingState object for the LoadType.REFRESH, we should mimic a scenario
     * where no data has previously been loaded (because we want to refresh), or it doesn't matter
     * if any data has been loaded before (because any previous data will be discarded)
     */
    @Test
    fun `initial refresh event saves new data properly in database`() = runTest {
        //Given
        val pagingState: PagingState<Int, CharacterEntity> = createPagingStateForInitialRefresh()
        //When
        val result = feedRemoteMediator.load(LoadType.REFRESH, pagingState)

        val localCharacters =
            ((localDatasource as? CharacterLocalDataSourceFake)?.getAllCharacters()?.load(
                PagingSource.LoadParams.Append(1, LOAD_SIZE, false)
            ) as? PagingSource.LoadResult.Page)?.data

        val pagingKeys = ((localDatasource as? CharacterLocalDataSourceFake)?.getPagingKeysById(2))

        //Then
        //checks if RemoteMediator gives success
        assertThat(result is RemoteMediator.MediatorResult.Success)
        assertThat((result as RemoteMediator.MediatorResult.Success)
            .endOfPaginationReached).isEqualTo(false)
        //ensures that RemoteMediator is saving data locally.
        assertThat(localCharacters?.isNotEmpty()).isEqualTo(true)
        //check if there is no previous data, last character saved has id 10 which is the page size.
        assertThat(localCharacters?.get(localCharacters.size - 1)?.id).isEqualTo(10)
        //ensures that RemoteMediator is saving PagingKeys properly
        assertThat(pagingKeys?.nextKey).isEqualTo("${QUERY_PAGE}2")
        assertThat(pagingKeys?.prevKey).isNull()
    }

    /**
     * Api pagination : data chunked by 20.
     * Steps before assertion
     *
     *      1- Get expected first index from next loaded page
     *      2- Get expected last index from next loaded page.
     *          Example: 3 pages already in database and 4 page is appended ->
     *              api gives: 61...80 characters
     *                 if real load size to screen is 10: expectedLastIndex = 70 because 61
     *                 is already an item to count.
     *
     *      3- Simulate that database has proper characters for "X" pages. Example:
     *      getDummyCharacterEntitiesForPages(3) -> 1...20...40...60 characters
     *      4- Simulate that database has proper page keys for last twenty characters
     *      getDummyPagingKeysForPages(3) -> 40...60 page keys.
     *      In that way, when load calls getPagingKeysForLastItem(state) will find last page
     *      of characters (41...60) and will get las page (60)
     *
     *      5- Simulate load state for appending. Gives PagingState with chunked data by
     *      pages. [[1...20], [21...40], [41...60]]
     *      6-Call load from remoteMediator. Will fetch next page of data to be appended.
     *      Take into account: api pagination is 20 by 20. So in local db will be saved in
     *      chunks by 20.
     *
     *      7- Simulate a 10 new appended characters data from local corresponding to next
     *      page.
     *          If last fetch page is 4: fetched data is 61...80 but 61...70 wil be loaded
     *          from local
     *
     * Assertions
     *      1- Check remoteMediator is success for append
     *      2- Check if new data has been appended / saved locally by checking first and last
     *      item to be presented into the screen.  Our real load size is 10.
     *
     */
    @Test
    fun `load append should properly manage pagination by saving and appending new data`() =
        runTest {
            val expectedFirstIndex = (FAKE_LOAD -1) * API_PAGE_SIZE + 1
            val expectedLastIndex = (expectedFirstIndex + LOAD_SIZE) -1
            val dummyLocalCharacter = getDummyCharacterEntitiesForPages()

            (localDatasource as? CharacterLocalDataSourceFake)?.setCharacters(dummyLocalCharacter)

            (localDatasource as? CharacterLocalDataSourceFake)?.setPagingKeys(
                getDummyPagingKeysForPages(FAKE_PAGES)
            )

            val pagingState: PagingState<Int, CharacterEntity> =
                createPagingStateForAppend(dummyLocalCharacter)

            val result = feedRemoteMediator.load(LoadType.APPEND, pagingState)

            val localCharacters =
                ((localDatasource as? CharacterLocalDataSourceFake)?.getAllCharacters()?.load(
                    PagingSource.LoadParams.Append(FAKE_LOAD, LOAD_SIZE, false)
                ) as? PagingSource.LoadResult.Page)?.data

            assertThat(result is RemoteMediator.MediatorResult.Success)
            assertThat(
                (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached
            ).isEqualTo(false)

            assertThat(localCharacters?.get(0)?.id).isEqualTo(expectedFirstIndex)
            assertThat(localCharacters?.get(localCharacters.size - 1)?.id).isEqualTo(expectedLastIndex)
        }

    @Test
    fun `network error, return MediatorResult Error`() = runTest {
        //Given
        (remoteDataSource as? CharacterRemoteDataSourceFake)?.remoteError =
            ApiResponseError(ApiUnifiedError.Generic(TEST_ERROR_MESSAGE))

        val dummyLocalCharacter = getDummyCharacterEntitiesForPages()

        (localDatasource as? CharacterLocalDataSourceFake)?.setCharacters(dummyLocalCharacter)

        (localDatasource as? CharacterLocalDataSourceFake)?.setPagingKeys(
            getDummyPagingKeysForPages(FAKE_PAGES)
        )
        val pagingState: PagingState<Int, CharacterEntity> =
            createPagingStateForAppend(dummyLocalCharacter)
        //When
        val result = feedRemoteMediator.load(LoadType.APPEND, pagingState)
        //Then
        assertThat(result is RemoteMediator.MediatorResult.Error).isEqualTo(true)
    }

    private fun createPagingStateForInitialRefresh(): PagingState<Int, CharacterEntity> {
        val pages = PagingSource.LoadResult.Page(
            data = emptyList<CharacterEntity>(),
            prevKey = null,
            nextKey = 2
        )
        return PagingState(
            pages = listOf(pages),
            anchorPosition = 0,
            config = PagingConfig(pageSize = API_PAGE_SIZE),
            leadingPlaceholderCount = 0
        )
    }

    /**
     * return : simulation of PagingState for appending event
     * The algorithm have the following considerations
     *      1- Assumes we already have some data loaded and split it into pages of size 20 which
     *      simulates api
     *      2- Construct list of chucked pages from the data
     */
    private fun createPagingStateForAppend(fakeLoadedData: List<CharacterEntity>): PagingState<Int, CharacterEntity> {
        val pagesData = fakeLoadedData.chunked(API_PAGE_SIZE)
        val pages = pagesData.mapIndexed { index, pageData ->
            PagingSource.LoadResult.Page(
                data = pageData,
                prevKey = if (index > 1) index - 1 else null,
                nextKey = if (index < pagesData.lastIndex) index + 1 else null
            )
        }
        return PagingState(
            pages = pages,
            anchorPosition = 0,
            config = PagingConfig(pageSize = API_PAGE_SIZE),
            leadingPlaceholderCount = 0
        )
    }

    private fun getDummyCharacterEntitiesForPages(): List<CharacterEntity> {
        return List(API_PAGE_SIZE * FAKE_PAGES) { i ->
            CharacterEntity(
                id = i + 1,
                name = "Character ${i + 1}",
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }
    }

    /**
     * Simulates having stored in database the last set of pages corresponding to last
     * 20 characters.
     * Algorithm calculates the start and end index corresponding to specific page
     * and gives paging keys with proper prev and next key.
     * Example: if already have 3 pages of characters loaded. 1...60
     * create PagingKeys for last twenty because remote mediator will use last character from
     * last page to fetch the next page to fetch and store.
     *      startIndex = (3 - 1) * 20 = 40
     *      endIndex = 3 * 20 = 60
     *      creates PagingKeys from 40 to 60 with corresponding id matching last twenty characters
     *      in the database.
     */
    private fun getDummyPagingKeysForPages(page: Int): List<PagingKeys>{
        val keyList = mutableListOf<PagingKeys>()
        val startIndex = (page -1) * API_PAGE_SIZE
        val enIndex = API_PAGE_SIZE * page
        for(id in startIndex..enIndex) {
            keyList.add(
                PagingKeys(
                    itemId = id.toLong(),
                    prevKey = if (page > 1) "$QUERY_PAGE${(page - 1)}" else null,
                    nextKey = "$QUERY_PAGE${(page + 1)}"
                )
            )
        }
        return keyList
    }
}