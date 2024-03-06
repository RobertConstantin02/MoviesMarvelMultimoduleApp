package com.example.common.screen.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.common.R
import com.example.common.screen.ScreenState
import com.example.common.screen.ScreenStateEvent
import com.example.domain_model.characterDetail.CharacterPresentationScreenBO
import com.example.domain_model.error.DomainApiUnifiedError
import com.example.domain_model.error.DomainUnifiedError
import com.example.presentation_mapper.BoToVoCharacterPresentationMapper.toCharacterPresentationScreenVO
import com.example.resources.UiText
import com.example.usecase.character.IGetCharacterDetailsUseCase
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val CHARACTER_ID = "1"
private const val INVALID_CHARACTER_ID = "-1"
private const val CHARACTER_ID_KEY = "characterId"
private const val LOCATION_ID = "1"
private const val INVALID_LOCATION_ID = "-1"
private const val LOCATION_ID_KEY = "locationId"
private const val ERROR_MESSAGE = "Error Message"

@OptIn(ExperimentalCoroutinesApi::class)
internal class DetailViewModelTest {
    private lateinit var getCharacterDetails: IGetCharacterDetailsUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var detailViewModel: DetailViewModel

    private var coroutineDispatcher: CoroutineDispatcher = spyk(Dispatchers.IO)
    val testDispatcher = StandardTestDispatcher()

    private lateinit var characterPresentationScreen: CharacterPresentationScreenBO


    /**
     * @mockkStatic(Dispatchers::class), tells the MockK agent to redirect all calls to the
     * Dispatchers class to MockK itself.
     * After interceptor setup, calling Dispatchers.IO or any other static method
     * or property of Dispatchers class will direct the call to MockK. MockK will then check to see if
     * any mocked behavior is defined for these calls (for example every { Dispatchers.IO }
     * returns testDispatcher).
     */
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        characterPresentationScreen = characterPresentationScreen()
        savedStateHandle = mockk()
        getCharacterDetails = mockk()

        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        every {
            coroutineDispatcher.fold<Any>(
                any(),
                any()
            )
        } answers { testDispatcher.fold(firstArg(), secondArg()) }

        detailViewModel = DetailViewModel(getCharacterDetails, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `given OnGetCharacterDetails, when getCharacterDetails success, then updates screen state success`() =
        runTest {
            //Given
            val expectedState =
                ScreenState.Success(characterPresentationScreen.toCharacterPresentationScreenVO())

            mockSavedStateHandle(CHARACTER_ID_KEY, CHARACTER_ID)
            mockSavedStateHandle(LOCATION_ID_KEY, LOCATION_ID)

            coEvery {
                getCharacterDetails.invoke(
                    match { params -> params.characterId > 0 && params.locationId > 0 },
                    any(),
                    success = any(),
                    error = any(),
                    empty = any()
                )
            } answers {
                arg<(CharacterPresentationScreenBO) -> Unit>(2).invoke(characterPresentationScreen)
                runs
            }

            assert(expectedState)
        }

    @Test
    fun `given OnGetCharacterDetails with invalid ids, when getCharacterDetails error, then updates screen state error`() =
        runTest {
            //Given
            val expectedState = ScreenState.Error(
                UiText.DynamicText(R.string.http_error_bad_request, 400, ERROR_MESSAGE),
                null
            )

            mockSavedStateHandle(CHARACTER_ID_KEY, INVALID_CHARACTER_ID)
            mockSavedStateHandle(LOCATION_ID_KEY, INVALID_LOCATION_ID)

            coEvery {
                getCharacterDetails.invoke(
                    match { params -> params.characterId <= 0 && params.locationId <= 0 },
                    any(),
                    success = any(),
                    error = any(),
                    empty = any()
                )
            } answers {
                arg<(
                    error: DomainUnifiedError,
                    CharacterPresentationScreenBO?
                ) -> Unit>(3).invoke(
                    DomainApiUnifiedError.Http.BadRequest(ERROR_MESSAGE, 400),
                    null
                )
                runs
            }

            assert(expectedState)
        }

    private fun mockSavedStateHandle(key: String, value: String) {
        every {
            savedStateHandle.get<String>(key)
        } returns value
    }

    private suspend fun <T> assert(expectedState: ScreenState<T>) {
        detailViewModel.characterDetailState.test {
            //When
            detailViewModel.onEvent(CharacterDetailScreenEvent.OnGetCharacterDetails())
            //Then
            val state1 = awaitItem()
            assertThat(state1).isInstanceOf(ScreenState.Loading::class.java)
            val state2 = awaitItem()
            assertThat(state2).isInstanceOf(ScreenState.Error::class.java)
            assertThat(state2).isEqualTo(expectedState)
        }
    }
}