package com.example.remote.character.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.example.api.model.character.FeedCharacterDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseSuccess
import com.example.remote.extension.GsonAdapterExt.fromJson
import com.example.remote.extension.toRickAndMortyService
import com.example.remote.fake.ApiErrorHandlerFake
import com.example.remote.util.FileUtil
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val ALL_CHARACTERS_JSON = "json/getAllCharacters.json"
private const val TEST_PAGE = 1

class CharacterRemoteDataSourceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiErrorHandler: ApiErrorHandlerFake
    private lateinit var rickAndMortyService: RickAndMortyService
    private lateinit var characterRemoteDataSource: CharacterRemoteDataSource

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiErrorHandler = ApiErrorHandlerFake()
        rickAndMortyService = mockWebServer.toRickAndMortyService(apiErrorHandler)
        characterRemoteDataSource = CharacterRemoteDataSource(rickAndMortyService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * @takeRequest : It will obtain the next HTTP request that has been made to the MockWebServer.
     * The returned RecordedRequest object, stored in the request variable, contains all the
     * information about the request including the method used (GET, POST, etc.), headers, path,
     * body (if any) and other details.
     */
    @Test
    fun `rick and morty service, get characters is success URL`() = runTest {
        //given
        val getAllCharactersJson = FileUtil.getJson(ALL_CHARACTERS_JSON)
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(getAllCharactersJson.orEmpty())
        )
        //when
        val characters = rickAndMortyService.getAllCharacters(TEST_PAGE)

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
        assertThat(characters).isEqualTo(ApiResponseSuccess(getAllCharactersJson?.fromJson<FeedCharacterDto>()))
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
     */
    @Test
    fun `rick and morty service, get characters returns ApiResponseEmpty`() = runTest {
        val mockResponse = MockResponse().setResponseCode(204).setBody("")
        mockWebServer.enqueue(mockResponse)

        val charactersResult = rickAndMortyService.getAllCharacters(TEST_PAGE)

        assertThat(charactersResult).isInstanceOf(ApiResponseEmpty::class.java)
    }
}