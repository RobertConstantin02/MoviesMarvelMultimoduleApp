package com.example.feature_favorites

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.common.R
import com.example.common.screen.ScreenState
import com.example.domain_model.error.DomainLocalUnifiedError
import com.example.domain_model.resource.DomainResource
import com.example.feature_favorites.paginator.FavoritePaginator
import com.example.feature_favorites.paginator.PaginatorFactory
import com.example.presentation_mapper.toCharacterVo
import com.example.resources.UiText
import com.example.test.character.CharacterUtil
import com.example.usecase.character.IGetFavoriteCharactersUseCase
import com.example.usecase.character.IUpdateCharacterIsFavoriteUseCase
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    private lateinit var getFavoriteCharacters: IGetFavoriteCharactersUseCase
    private lateinit var updateChacterIsFavorite: IUpdateCharacterIsFavoriteUseCase
    private lateinit var paginationFactory: PaginatorFactory

    private lateinit var viewModel: FavoritesViewModel

    private var coroutineDispatcher: CoroutineDispatcher = spyk(Dispatchers.IO)
    val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        getFavoriteCharacters = mockk()
        updateChacterIsFavorite = mockk()

        paginationFactory = FavoritePaginatorFactoryFake()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every {
            coroutineDispatcher.fold<Any>(
                any(),
                any()
            )
        } answers { testDispatcher.fold(firstArg(), secondArg()) }

        viewModel = FavoritesViewModel(
            getFavoriteCharacters, updateChacterIsFavorite, paginationFactory
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `given success OnLoadData events, when data base has data, then state success`() = runTest {
        //Given
        val expected = CharacterUtil.favoriteCharacters.toList().map {
            it.toCharacterVo()
        }

        coEvery {
            getFavoriteCharacters.invoke(match { it.page >= 0 })
        } returns flowOf(DomainResource.success(CharacterUtil.favoriteCharacters.toList()))

        viewModel.favoritesState.test {
            //--- first load event ---
            viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
            val firstLoading = awaitItem()
            assertThat(firstLoading).isInstanceOf(ScreenState.Loading::class.java)
            val initialAppend = awaitItem()
            assertThat(initialAppend).isInstanceOf(ScreenState.Success::class.java)
            assertThat((initialAppend as? ScreenState.Success)?.data).isEqualTo(expected.take(10))

            //--- second load event ---
            viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
            val secondAppend = awaitItem()
            assertThat(secondAppend).isInstanceOf(ScreenState.Success::class.java)
            assertThat((secondAppend as? ScreenState.Success)?.data).isEqualTo(expected)
        }
    }

    @Test
    fun `given OnLoadData event, when database empty, then state empty`() = runTest {
        val expected =
            ScreenState.Empty<Nothing>(UiText.StringResources(R.string.empty_favorite_list))

        coEvery {
            getFavoriteCharacters.invoke(match { it.page >= 0 })
        } returns flowOf(DomainResource.successEmpty())

        viewModel.favoritesState.test {
            //--- first load event ---
            viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
            val firstLoading = awaitItem()
            assertThat(firstLoading).isInstanceOf(ScreenState.Loading::class.java)
            val initialAppend = awaitItem()
            assertThat(initialAppend).isInstanceOf(ScreenState.Empty::class.java)
            assertThat((initialAppend as? ScreenState.Empty)?.message).isEqualTo(expected.message)
        }
    }

    @Test
    fun `given OnLoadData event, when database error, then state error`() = runTest {
        val expected =
            ScreenState.Error(UiText.StringResources(R.string.local_db_read_error), null)

        coEvery {
            getFavoriteCharacters.invoke(match { it.page >= 0 })
        } returns flowOf(DomainResource.error(DomainLocalUnifiedError.Reading))

        viewModel.favoritesState.test {
            //--- first load event ---
            viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
            val firstLoading = awaitItem()
            assertThat(firstLoading).isInstanceOf(ScreenState.Loading::class.java)
            val initialAppend = awaitItem()
            assertThat(initialAppend).isInstanceOf(ScreenState.Error::class.java)
            assertThat((initialAppend as? ScreenState.Error)?.message).isEqualTo(expected.message)
        }
    }

    @Test
    fun `given OnRemoveFavorite event, when database success, then state success`() = runTest {
        val expected = CharacterUtil.favoriteCharacters.toList().take(2)

        coEvery {
            getFavoriteCharacters.invoke(match { it.page >= 0 })
        } returns flowOf(DomainResource.success(expected))

        coEvery {
            updateChacterIsFavorite.invoke(
                input = any(),
                coroutineScope = any(),
                success = any(),
                error = any()
            )
        } answers {
            arg<(Unit) -> Unit>(2).invoke(Unit)
            runs
        }

        viewModel.favoritesState.test {
            //--- first load event ---
            viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
            val firstLoading = awaitItem()
            assertThat(firstLoading).isInstanceOf(ScreenState.Loading::class.java)
            val initialAppend = awaitItem()
            assertThat(initialAppend).isInstanceOf(ScreenState.Success::class.java)
            assertThat((initialAppend as? ScreenState.Success)?.data).isEqualTo(expected.map { it.toCharacterVo() })

            viewModel.onEvent(FavoritesScreenEvent.OnRemoveFavorite(false, 2))
            val removeEvent = awaitItem()
            assertThat((removeEvent as? ScreenState.Success)?.data).isEqualTo(
                listOf(
                    expected.first().toCharacterVo()
                )
            )
        }
    }

    /**
     * @getFavoriteCharacters is called two times. First one simulates a full load and second one
     * loads remaining characters triggering the end of pagination.
     */
    @Test
    fun `given success OnLoadData events, when data base has no more data, then pagination state end`() =
        runTest {
            val expected = CharacterUtil.favoriteCharacters.toList().take(12).map {
                it.toCharacterVo()
            }

            coEvery {
                getFavoriteCharacters.invoke(match { it.page >= 0 })
            } answers {
                if (arg<IGetFavoriteCharactersUseCase.Params>(0).page == 0) {
                    flowOf(
                        DomainResource.success(
                            CharacterUtil.favoriteCharacters.toList().take(10)
                        )
                    )
                } else {
                    flowOf(
                        DomainResource.success(
                            CharacterUtil.favoriteCharacters.toList().subList(10, 12)
                        )
                    )
                }
            }

            viewModel.favoritesState.test {
                //--- first load event ---
                viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
                val firstLoading = awaitItem()
                assertThat(firstLoading).isInstanceOf(ScreenState.Loading::class.java)
                val initialAppend = awaitItem()
                assertThat(initialAppend).isInstanceOf(ScreenState.Success::class.java)
                assertThat((initialAppend as? ScreenState.Success)?.data).isEqualTo(expected.take(10))

                //--- second load event ---
                viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
                viewModel.paginationState.test {
                    awaitItem() //skip idle
                    awaitItem() //skip loading
                    val paginationEnd = awaitItem()
                    assertThat(paginationEnd).isInstanceOf(FavoritePaginator.State.End::class.java)
                }
                val secondAppend = awaitItem()
                assertThat(secondAppend).isInstanceOf(ScreenState.Success::class.java)
                assertThat((secondAppend as? ScreenState.Success)?.data).isEqualTo(expected)
            }
        }
}