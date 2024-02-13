package com.example.remote.character.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.api.model.character.CharacterDto
import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponse
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.ApiUnifiedError
import com.example.remote.extension.toRickAndMortyService
import com.example.remote.fake.ApiErrorHandlerFake
import com.example.test.character.ALL_CHARACTERS_FIRST_PAGE_JSON
import com.example.test.character.CharacterUtil
import com.example.test.character.EMPTY_JSON
import com.example.test.FileUtil
import com.example.test.character.RESULTS_EMPTY_EPISODES_JSON
import com.example.test.character.RESULTS_EMPTY_JSON
import com.example.test.character.RESULTS_NULL_JSON
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection
import java.net.URL

private const val TEST_PAGE = 1
private const val BAD_TEST_PAGE = -1
private const val CHARACTER_ID_TEST = 1
private const val PAGE_PARAMETER = "page"
private const val PAGE_PARAMETER_PLUS_VALUE = "page=2"
const val TEST_ERROR_MESSAGE = "Error Test"
class CharacterRemoteDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiErrorHandler: ApiErrorHandlerFake
    private lateinit var service: RickAndMortyService
    private lateinit var characterRemoteDataSource: CharacterRemoteDataSource

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiErrorHandler = ApiErrorHandlerFake()
        service = mockWebServer.toRickAndMortyService(apiErrorHandler)
        characterRemoteDataSource = CharacterRemoteDataSource(service)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * Cheks if the request made by getAllCharacters call is GET with defined parameters
     * @takeRequest : It will obtain the next HTTP request that has been made to the MockWebServer.
     * The returned RecordedRequest object, stored in the request variable, contains all the
     * information about the request including the method used (GET, POST, etc.), headers, path,
     * body (if any) and other details.
     */
    @Test
    fun `rick and morty service, get characters is success request`() = runTest {
        //given
        val getAllCharactersJson = FileUtil.getJson(ALL_CHARACTERS_FIRST_PAGE_JSON)
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(getAllCharactersJson.orEmpty())
        )
        //when
        val characters = characterRemoteDataSource.getAllCharacters(TEST_PAGE)

        val request = mockWebServer.takeRequest()
        val requestUrl = request.requestUrl
        val queryParameters = requestUrl?.queryParameterNames?.toList()

        //then
        assertThat(request.method).isEqualTo("GET")
        assertThat(requestUrl?.encodedPath).isEqualTo("/api/character/")
        with(queryParameters) {
            assertThat(this?.size).isEqualTo(1)
            assertThat(this?.get(0)).isEqualTo("page")
        }
        assertThat(characters).isEqualTo(ApiResponseSuccess(CharacterUtil.expectedSuccessCharactersFirstPage))
    }

    /**
     * @setResponseCode(204).setBody("") / setResponseCode(204) -> 2xx is treated as successful by Retrofit.
     * So we will get response.isSuccessFull == true. The body will be null in both cases.
     *
     * @setResponseCode(200).setBody("") -> Response is successful but When your client
     * (Retrofit, in this case) tries to read the response body, it finds that there's no more
     * data to read (end of file/stream), hence EOFException is thrown.
     *
     * @setResponseCode(204).setBody("{}") -> ProtocolException. HTTP 204 (No Content) status code
     * indicates that the server has successfully fulfilled the request and that there is no
     * additional content to send in the response payload body. This generally means the server
     * has processed the request successfully, but is not returning any content. Hence, the
     * Content-Length should be '0'.
     * When you use setBody("{}") with response code 204, you are adding a body of 2 characters
     * ('{' and '}') to the response, which is in violation of the rules of using status 204.
     * Should be null the content but an empty json is available.
     *
     * @Problem encountered: ApiResponseEmpty() is a class that has nothing inside to be compared.
     * This differs from ApiResponseSuccess or ApiResponseError which are data classes that
     * can be compared for equal content (==). In the case of ApiResponseEmpty() the comparison is
     * taking to the memory location point (===) -> Use isInstanceOf
     *
     */
    @Test
    fun `rick and morty service, get characters is ApiResponseEmpty`() = runTest {
        //Given
        val expected: ApiResponse<FeedCharacterDto> = ApiResponseEmpty()
        mockWebServer.enqueue(MockResponse().setResponseCode(204).setBody(""))
        //When
        val result = characterRemoteDataSource.getAllCharacters(TEST_PAGE)
        //Then
        assertThat(result).isInstanceOf(expected::class.java)
    }

    @Test
    fun `rick and morty service, get characters with wrong page number is ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponse<FeedCharacterDto> = ApiResponseSuccess(CharacterUtil.expectedSuccessCharactersFirstPage)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(FileUtil.getJson(ALL_CHARACTERS_FIRST_PAGE_JSON).orEmpty()))
        //When
        val result = characterRemoteDataSource.getAllCharacters(BAD_TEST_PAGE)
        //Then
        assertThat(result).isEqualTo(expected)
        assertThat(URL((result as? ApiResponseSuccess)?.body?.info?.next).query).isEqualTo(PAGE_PARAMETER_PLUS_VALUE)
    }

    @Test
    fun `rick and morty service, get characters with FeedCharacterDto results null is ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponse<FeedCharacterDto> = ApiResponseSuccess(CharacterUtil.expectedResultsNullResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(FileUtil.getJson(RESULTS_NULL_JSON).orEmpty()))
        //When
        val result = characterRemoteDataSource.getAllCharacters(TEST_PAGE)
        //Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `rick and morty service, get characters with FeedCharacterDto results empty is ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponse<FeedCharacterDto> = ApiResponseSuccess(CharacterUtil.expectedResultsEmptyResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(FileUtil.getJson(RESULTS_EMPTY_JSON).orEmpty()))
        //When
        val result = characterRemoteDataSource.getAllCharacters(TEST_PAGE)
        //Then
        assertThat(result).isEqualTo(expected)
        assert((result as? ApiResponseSuccess<FeedCharacterDto>)?.body?.results?.isNotEmpty() == false)
    }

    @Test
    fun `rick and morty service, get characters with character episodes empty is ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponse<FeedCharacterDto> = ApiResponseSuccess(CharacterUtil.expectedResultsEmptyResponse)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(FileUtil.getJson(RESULTS_EMPTY_EPISODES_JSON).orEmpty()))
        //When
        val result = characterRemoteDataSource.getAllCharacters(TEST_PAGE)
        //Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `rick and morty service, get characters is HTTP_UNAUTHORIZED`() = runTest {

        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Http.Unauthorized(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_UNAUTHORIZED)

        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
        mockWebServer.enqueue(mockResponse)

        val charactersResult = characterRemoteDataSource.getAllCharacters(TEST_PAGE)

        assertThat(charactersResult).isInstanceOf(ApiResponseError::class.java)
        assertThat(charactersResult).isEqualTo(ApiResponseError(ApiUnifiedError.Http.Unauthorized(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_UNAUTHORIZED)))
        assertThat((charactersResult as ApiResponseError).apiUnifiedError).isEqualTo(ApiUnifiedError.Http.Unauthorized(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_UNAUTHORIZED))
    }

    @Test
    fun `rick and morty service, get characters returns HTTP_NOT_FOUND`() = runTest {
        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Http.NotFound(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_NOT_FOUND)

        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        mockWebServer.enqueue(mockResponse)

        val charactersResult = characterRemoteDataSource.getAllCharacters(TEST_PAGE)

        assertThat(charactersResult).isInstanceOf(ApiResponseError::class.java)
        assertThat(charactersResult).isEqualTo(ApiResponseError(ApiUnifiedError.Http.NotFound(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_NOT_FOUND)))
        assertThat((charactersResult as ApiResponseError).apiUnifiedError).isInstanceOf(ApiUnifiedError.Http.NotFound::class.java)
    }

    @Test
    fun `rick and morty service, get characters returns HTTP_INTERNAL_ERROR`() = runTest {
        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Http.InternalErrorApi(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_INTERNAL_ERROR)

        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
        mockWebServer.enqueue(mockResponse)

        val charactersResult = characterRemoteDataSource.getAllCharacters(TEST_PAGE)

        assertThat(charactersResult).isEqualTo(ApiResponseError(ApiUnifiedError.Http.InternalErrorApi(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_INTERNAL_ERROR)))
        assertThat(charactersResult).isInstanceOf(ApiResponseError::class.java)
        assertThat((charactersResult as ApiResponseError).apiUnifiedError).isInstanceOf(ApiUnifiedError.Http.InternalErrorApi::class.java)
    }

    @Test
    fun `rick and morty service, get characters returns HTTP_BAD_REQUEST`() = runTest {
        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Http.BadRequest(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_BAD_REQUEST)

        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        mockWebServer.enqueue(mockResponse)

        val charactersResult = characterRemoteDataSource.getAllCharacters(TEST_PAGE)

        assertThat(charactersResult).isEqualTo(ApiResponseError(ApiUnifiedError.Http.BadRequest(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_BAD_REQUEST)))
        assertThat(charactersResult).isInstanceOf(ApiResponseError::class.java)
        assertThat((charactersResult as ApiResponseError).apiUnifiedError).isInstanceOf(ApiUnifiedError.Http.BadRequest::class.java)
    }

    @Test
    fun `rick and morty service, get characters returns GENERIC`() = runTest {
        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Generic(message = "Client Error")

        val mockResponse = MockResponse().setResponseCode(HttpURLConnection.HTTP_REQ_TOO_LONG)
        mockWebServer.enqueue(mockResponse)

        val charactersResult = characterRemoteDataSource.getAllCharacters(TEST_PAGE)
        assertThat(charactersResult).isEqualTo(ApiResponseError(ApiUnifiedError.Generic(message = "Client Error")))
        assertThat(charactersResult).isInstanceOf(ApiResponseError::class.java)
        assertThat((charactersResult as ApiResponseError).apiUnifiedError).isInstanceOf(ApiUnifiedError.Generic::class.java)
    }

    /**
     * ---------------------> Character test
     */

    /**
     * Use of mockWebServer.
     * Use of mockk that mocks the production call to getCharacterById.
     */
    @Test
    fun `get character by id, success empty character`() = runTest {
        //Given
        val expected: ApiResponseSuccess<CharacterDto> = ApiResponseSuccess(CharacterUtil.expectedEmptyCharacterResponse)
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(FileUtil.getJson(EMPTY_JSON).orEmpty())
        )
        //When
        val actual: ApiResponse<CharacterDto> =
            characterRemoteDataSource.getCharacterById(CHARACTER_ID_TEST)
        //Then
        assertThat(actual).isEqualTo(expected)
    }
}