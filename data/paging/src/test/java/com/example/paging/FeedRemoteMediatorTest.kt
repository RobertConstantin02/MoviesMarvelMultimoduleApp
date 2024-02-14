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
import assertk.assertions.isNullOrEmpty
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.remote.character.datasource.ICharacterRemoteDataSource
import com.example.database.detasource.character.fake.CharacterLocalDataSourceFake
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.remote.character.datasource.fake.CharacterRemoteDataSourceFake
import com.example.test.character.CharacterUtil
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

const val PAGE_SIZE = 10

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
        assertThat(localCharacters?.get(localCharacters.size - 1)?.id).isEqualTo(PAGE_SIZE)
        //ensures that RemoteMediator is saving PagingKeys properly
        assertThat(pagingKeys?.nextKey).isEqualTo("https://rickandmortyapi.com/api/character/?page=2")
        assertThat(pagingKeys?.prevKey).isNull()
    }

    /**
     * First simulates that database has characters and pagingKeys, for subsequent appending
     */
    @Test
    fun `load append should properly manage pagination by saving and appending new data`() =
        runTest {
            //we have to set local data and simultate that databse has already data with characters and ids.
            //then chec if added ids from last characters are expected. Like if first is 11 and last is 20

            (localDatasource as? CharacterLocalDataSourceFake)?.setCharacters(
                getDummyCharacterEntities()
            )

            (localDatasource as? CharacterLocalDataSourceFake)?.setPagingKeys(
                getDummyPagingKeysForPage(1)
            )

            val pagingState: PagingState<Int, CharacterEntity> =
                createPagingStateForAppend() //make dynamic with more pages

            val result = feedRemoteMediator.load(LoadType.APPEND, pagingState)

            //getCharacters for second page, which is the one newly appended
            val localCharacters =
                ((localDatasource as? CharacterLocalDataSourceFake)?.getAllCharacters()?.load(
                    PagingSource.LoadParams.Append(2, 10, false)
                ) as? PagingSource.LoadResult.Page)?.data

            assertThat(result is RemoteMediator.MediatorResult.Success)
            assertThat(
                (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached
            ).isEqualTo(false)


//            assertThat(localCharacters?.get(0)?.id).isEqualTo(11)
//            assertThat(localCharacters?.get(localCharacters.size - 1)?.id).isEqualTo(20)

        }

    // TODO: check if this is worth it?
    private fun createPagingStateForAppend(): PagingState<Int, CharacterEntity> {
        // Let's assume we already have some data loaded
        val characterEntities = getDummyCharacterEntities()

        // Let's split it into pages of size 10, for example
        val pageSize = PAGE_SIZE
        val pagesData = characterEntities.chunked(pageSize)

        // Now let's construct pages from the data
        val pages = pagesData.mapIndexed { index, pageData ->
            PagingSource.LoadResult.Page(
                data = pageData,
                prevKey = if (index > 1) index - 1 else null,
                nextKey = if (index < pagesData.lastIndex) index + 1 else null
            )
        }

        println("-----> page ${pages.get(0).nextKey}")

        // Assuming that we have scrolled at the end of the page of 10 elements
        val anchorPosition = characterEntities.size

        val config = PagingConfig(pageSize = pageSize)

        return PagingState(
            pages = pages,
            anchorPosition = anchorPosition,
            config = config,
            leadingPlaceholderCount = 0
        )
    }

    private fun getDummyCharacterEntities(): List<CharacterEntity> {
        return List(10) { i ->
            CharacterEntity(
                id = i + 1,
                name = "Character $i",
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

    private fun getDummyPagingKeysForPage(page: Int) = List(10) { i ->
        //make ids proper for page : algorithm
        PagingKeys(
            itemId = (i + 1).toLong(),
            prevKey = if (page > 1) "https://rickandmortyapi.com/api/character/?page=${(page - 1)}" else null,
            nextKey = "https://rickandmortyapi.com/api/character/?page=${(page + 1)}"
        )
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
            config = PagingConfig(pageSize = PAGE_SIZE),
            leadingPlaceholderCount = 0
        )
    }

//    private fun createPagingStateForInitialRefresh(): PagingState<Int, CharacterEntity> {
//        // Let's assume we don't have some data loaded
//        val characterEntities = emptyList<CharacterEntity>()
//
//        // Let's split it into pages of size 20 for example
//        val pageSize = 10
//        val pagesData = characterEntities.chunked(pageSize)
//
//        // Now let's construct pages from the data
//        val pages = pagesData.mapIndexed { index, characters ->
//            PagingSource.LoadResult.Page(
//                data = characters,
//                prevKey = if (index > 0) index -1 else null,
//                nextKey = if (index < pagesData.lastIndex) index + 1 else null
//            )
//        }
//
//        return PagingState(
//            pages = pages,
//            anchorPosition = 0,
//            config = PagingConfig(pageSize = pageSize),
//            leadingPlaceholderCount = 0
//        )
//    }

//    private fun getDummyCharacterEntities(): List<CharacterEntity> {
//        return List(100) { index ->
//            CharacterEntity(id = index)
//        }
//    }
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