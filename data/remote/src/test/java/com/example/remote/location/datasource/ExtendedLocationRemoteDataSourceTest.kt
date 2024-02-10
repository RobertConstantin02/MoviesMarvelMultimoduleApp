package com.example.remote.location.datasource

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.api.model.location.ExtendedLocationDto
import com.example.api.network.RickAndMortyService
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.ApiUnifiedError
import com.example.remote.extension.toRickAndMortyService
import com.example.remote.fake.ApiErrorHandlerFake
import com.example.test.character.CharacterUtil
import com.example.test.character.EXTENDED_LOCATION
import com.example.test.FileUtil
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection

private const val LOCATION_ID = 3
class ExtendedLocationRemoteDataSourceTest {
//    private lateinit var extendedLocationRemoteDataSource: ExtendedLocationRemoteDataSource
//    private lateinit var service: RickAndMortyService


    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiErrorHandler: ApiErrorHandlerFake
    private lateinit var service: RickAndMortyService
    private lateinit var extendedLocationRemoteDataSource: ExtendedLocationRemoteDataSource

    @BeforeEach
    fun setUp() {
//        service = mockk()
//        extendedLocationRemoteDataSource = ExtendedLocationRemoteDataSource(service)

        mockWebServer = MockWebServer()
        mockWebServer.start()
        apiErrorHandler = ApiErrorHandlerFake()
        service = mockWebServer.toRickAndMortyService(apiErrorHandler)
        extendedLocationRemoteDataSource = ExtendedLocationRemoteDataSource(service)
    }

    @Test
    fun `service get location, is success request url`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(FileUtil.getJson(EXTENDED_LOCATION).orEmpty())
        )

        val result = service.getLocation(LOCATION_ID)

        val request = mockWebServer.takeRequest()
        val requestUrl = request.requestUrl
        val queryParams = requestUrl?.queryParameterNames

        assertThat(request.method).isEqualTo("GET")
        assertThat(requestUrl?.encodedPath).isEqualTo("/api/location/3")
        with(queryParams) {
            assertThat(this?.size).isEqualTo(0)
        }
        assertThat(result).isEqualTo(ApiResponseSuccess(CharacterUtil.expectedSuccessLocation))
    }

    @Test
    fun `service get location, returns ApiResponseSuccess`() = runTest {
        //Given
        val expected: ApiResponseSuccess<ExtendedLocationDto> = ApiResponseSuccess(CharacterUtil.expectedSuccessLocation)
        coEvery {
            service.getLocation(LOCATION_ID)
        } returns ApiResponseSuccess(expected.body)
        //When
        val result = extendedLocationRemoteDataSource.getLocation(LOCATION_ID)
        //Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `service get location, return ApiResponseError`() = runTest {
        //GIVEN
        val expectedErrorMessage = ApiUnifiedError.Http.NotFound(message = "Location not found")
        coEvery {
            service.getLocation(any())
        } returns ApiResponseError(ApiUnifiedError.Http.NotFound(message = "Location not found"))
        //When
        val result = extendedLocationRemoteDataSource.getLocation(-1)
        //Then
        assertThat((result as? ApiResponseError)?.apiUnifiedError?.message).isEqualTo(expectedErrorMessage.message)
    }

    @Test
    fun `service get location, return ApiResponseError mockWebServer`() = runTest {
        //GIVEN
        apiErrorHandler.apiUnifiedError = ApiUnifiedError.Http.NotFound(message = "Client Error")
        mockWebServer.enqueue(
            MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        )
        //When
        val result = extendedLocationRemoteDataSource.getLocation(-1)
        //Then
        assertThat((result as? ApiResponseError)?.apiUnifiedError).isEqualTo(ApiUnifiedError.Http.NotFound(message = "Client Error"))
    }
}