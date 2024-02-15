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
                PagingSource.LoadParams.Append(1, 10, false)
            ) as? PagingSource.LoadResult.Page)?.data

        val pagingKeys = ((localDatasource as? CharacterLocalDataSourceFake)?.getPagingKeysById(2))

        //Then
        //checks if RemoteMediator gives success
        assertThat(result is RemoteMediator.MediatorResult.Success)
        assertThat((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached).isEqualTo(
            false
        )
        //ensures that RemoteMediator is saving data locally.
        assertThat(localCharacters?.isNotEmpty()).isEqualTo(true)
        //check if there is no previous data, last character saved has id 10 which is the page size.
        assertThat(localCharacters?.get(localCharacters.size - 1)?.id).isEqualTo(API_PAGE_SIZE)
        //ensures that RemoteMediator is saving PagingKeys properly
        assertThat(pagingKeys?.nextKey).isEqualTo("https://rickandmortyapi.com/api/character/?page=2")
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


//
//private fun createPagingStateForRefresh(): PagingState<Int, CharacterEntity> {
//    // Let's assume we already have some data loaded
//    val characterEntities = getDummyCharacterEntities()
//
//    // Let's split it into pages of size 20 for example
//    val pageSize = 20
//    val pagesData = characterEntities.chunked(pageSize)
//
//    // Now let's construct pages from the data
//    val pages = pagesData.map {
//        PagingSource.LoadResult.Page(
//            data = it,
//            prevKey = null, // prevKey and nextKey can be adjusted according to your data
//            nextKey = null
//        )
//    }
//
//    // Assuming that we have scrolled let's say halfway through the loaded data
//    val anchorPosition = characterEntities.size / 2
//
//    val config = PagingConfig(pageSize = pageSize)
//
//    return PagingState(
//        pages = pages,
//        anchorPosition = anchorPosition,
//        config = config,
//        leadingPlaceholderCount = 0
//    )
//}
//
//private fun getDummyCharacterEntities(): List<CharacterEntity> {
//    // Implementation of this method depends on your `CharacterEntity`,
//    // For now, let's assume it's a data class that has at least an id field
//    return List(100) { index ->
//        CharacterEntity(id = index)
//    }
//}
//
//private fun createPagingStateForRefresh(): PagingState<Int, CharacterEntity> {
//    // Representing no data
//    val pages = emptyList<PagingSource.LoadResult<Int, CharacterEntity>>()
//
//    // Config with a pageSize, you can adjust this
//    val config = PagingConfig(20)
//
//    return PagingState(
//        pages = pages,
//        anchorPosition = null, // As we're refreshing, we don't care about any previously loaded pages
//        config = config,
//        leadingPlaceholderCount = 0
//    )
//}
//
//class FeedRemoteMediatorTest2 {
//
//    // Replace with your actual fakes
//    private lateinit var localDataSource: FakeLocalDataSource
//    private lateinit var remoteDataSource: FakeRemoteDataSource
//    private lateinit var mediator: FeedRemoteMediator
//
//    @BeforeEach
//    fun setup() {
//        localDataSource = FakeLocalDataSource()
//        remoteDataSource = FakeRemoteDataSource(data = feedCharacterDtoData)
//        mediator = FeedRemoteMediator(localDataSource, remoteDataSource)
//    }
//
//    @Test
//    fun `Load refresh should call remote data source and store its data locally`() = runBlockingTest {
//        val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//
//        mediator.load(LoadType.REFRESH, pagingState)
//
//        // Assert that remoteDataSource was called and the resulting data was stored locally
//        assertThat(remoteDataSource.calledTimes).isEqualTo(1)
//        assertThat(localDataSource.savedCharacters).isEqualTo(feedCharacterDtoData.results?.map { it.toCharacterEntity() })
//    }
//
//    @Test
//    fun `Load append should call remote data source and store its data locally`() = runBlockingTest {
//        val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//
//        remoteDataSource.data = updatedFeedCharacterDtoData // new data for the second page
//        mediator.load(LoadType.APPEND, pagingState)
//
//        // Assert that remoteDataSource was called and its data saved in localDataSource
//        assertThat(remoteDataSource.calledTimes).isEqualTo(1)
//        assertThat(localDataSource.savedCharacters).isEqualTo(updatedFeedCharacterDtoData.results?.map { it.toCharacterEntity() })
//    }
//
//    @Test
//    fun `Load prepend should not call remote data source`() = runBlockingTest {
//        val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//
//        mediator.load(LoadType.PREPEND, pagingState)
//
//        // Assert that remoteDataSource was not called
//        assertThat(remoteDataSource.calledTimes).isEqualTo(0)
//    }
//
//    // Similar tests could be written for other cases as well, such as when an error occurs or when the data is empty.
//
//    private fun createPagingState(): PagingState<Int, CharacterEntity> {
//        // Create a PagingState using dummy data. Adjust this according to your own needs.
//        // You may need to mock this depending on your use case.
//    }
//}


//private fun createPagingState(): PagingState<Int, CharacterEntity> {
//    // Get dummy character entities and create pages
//    val characterEntities = getDummyCharacterEntities()
//    val pageSize = 20
//    val pages = characterEntities.chunked(pageSize).map { PagingSource.LoadResult.Page(it, null, it.lastOrNull()?.id) }
//
//    // Get the last accessed index in the list
//    val lastItemIndex = characterEntities.size - 1
//
//    return PagingState(
//        pages = pages,
//        anchorPosition = lastItemIndex,
//        config = PagingConfig(pageSize = pageSize),
//        leadingPlaceholderCount = 0
//    )
//}
//


//@Test
//fun `load refresh should clear old data and store new data properly`() = runBlockingTest {
//    // Given a PagingState and a remote data source with some stored data
//    val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//    val oldData = remoteDataSource.data
//
//    // When load is called with LoadType.REFRESH
//    val result = mediator.load(LoadType.REFRESH, pagingState)
//
//    // We should fetch new data from the remote data source - verify new data is fetched from remoteDataSource
//    assertThat(remoteDataSource.calledTimes).isEqualTo(1)
//
//    // Local data source should clear the old data - can check if a clear function is called if you have it in your local data source
//    assertThat(localDataSource.calledClearTimes).isEqualTo(1)
//
//    // And store the new data - Use containsExactly to make sure all and only the fetched items are stored
//    assertThat(localDataSource.savedCharacters).containsExactly(*oldData.results?.map { it.toCharacterEntity() }.orEmpty().toTypedArray())
//
//    // The result should indicate success and have endOfPaginationReached appropriately
//    assert(result is MediatorResult.Success)
//    assertThat((result as MediatorResult.Success).endOfPaginationReached).isEqualTo(oldData.info.next == null)
//}
//
//@Test
//fun `load append should properly manage pagination`() = runBlockingTest {
//    // Given a PagingState representing some already loaded data
//    val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//    val oldData = remoteDataSource.data
//
//    // When load is called with LoadType.APPEND
//    val result = mediator.load(LoadType.APPEND, pagingState)
//
//    // We should fetch new data from the remote data source - verify that new data is fetched from remoteDataSource
//    assertThat(remoteDataSource.calledTimes).isEqualTo(1)
//
//    // And append it to the existing data in the local data source - Use containsAtLeast to verify that all old items are still present along with new ones
//    assertThat(localDataSource.savedCharacters).containsAtLeast(*oldData.results?.map { it.toCharacterEntity() }.orEmpty().toTypedArray())
//
//    // The result should indicate success and have endOfPaginationReached appropriately
//    assert(result is MediatorResult.Success)
//    assertThat((result as MediatorResult.Success).endOfPaginationReached).isEqualTo(oldData.info.next == null)
//}
//
//@Test
//fun `network errors should be properly handled`() = runBlockingTest {
//    // Given that a network error will occur when fetching data
//    remoteDataSource.shouldReturnError = true
//
//    // Create a PagingState and load data
//    val pagingState: PagingState<Int, CharacterEntity> = createPagingState()
//    val result = mediator.load(LoadType.REFRESH, pagingState)
//
//    // The load result should be be an error
//    assertThat(result is MediatorResult.Error).isEqualTo(true)
//}