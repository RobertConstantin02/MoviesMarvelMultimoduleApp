package com.example.retrofit.api_error_handler

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.core.remote.ApiUnifiedError
import com.example.test.GsonAdapterExt.toJson
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

const val TEST_ERROR_MESSAGE = "Error Message"

class ApiErrorHandlerImplTest {
    private lateinit var apiErrorHandlerImpl: ApiErrorHandlerImpl

    @BeforeEach
    fun setUp() {
        apiErrorHandlerImpl = ApiErrorHandlerImpl()
    }

    /**
     * IOException is implemented by different classes like SocketTimeoutException.
     * Throwable is parent class of Exception which in turn is parent of IOException
     * invoke(t: Throwable) -> then SocketTimeoutException() as parameter
     */
    @Test
    fun `api error handler, return IOException SocketTimeoutException`() {
        testIoException(
            ApiUnifiedError.Connectivity.TimeOut("Error Test"),
            SocketTimeoutException("Error Test")
        )
    }

    @Test
    fun `api error handler, return IOException ConnectException`() {
        testIoException(
            ApiUnifiedError.Connectivity.NoConnection("Error Test"),
            ConnectException("Error Test")
        )
    }

    @Test
    fun `api error handler, return IOException UnknownHostException`() {
        testIoException(
            ApiUnifiedError.Connectivity.HostUnreachable("Error Test"),
            UnknownHostException("Error Test")
        )
    }

    @Test
    fun `api error handler, return IOException Generic`() {
        testIoException(
            ApiUnifiedError.Generic("Error Test"),
            UnknownServiceException("Error Test")
        )
    }

    @Test
    fun `api error handler, return HttpException HTTP_UNAUTHORIZED`() {
        testHttpException(
            ApiUnifiedError.Http.Unauthorized(
                TEST_ERROR_MESSAGE,
                HttpURLConnection.HTTP_UNAUTHORIZED
            ), HttpURLConnection.HTTP_UNAUTHORIZED
        )
    }

    //
    @Test
    fun `api error handler, return HttpException HTTP_NOT_FOUND`() {
        testHttpException(
            ApiUnifiedError.Http.NotFound(
                TEST_ERROR_MESSAGE,
                HttpURLConnection.HTTP_NOT_FOUND
            ), HttpURLConnection.HTTP_NOT_FOUND
        )
    }

    @Test
    fun `api error handler, return HttpException HTTP_INTERNAL_ERROR`() {
        testHttpException(
            ApiUnifiedError.Http.InternalErrorApi(
                TEST_ERROR_MESSAGE,
                HttpURLConnection.HTTP_INTERNAL_ERROR
            ), HttpURLConnection.HTTP_INTERNAL_ERROR
        )
    }

    @Test
    fun `api error handler, return HttpException HTTP_BAD_REQUEST`() {
        testHttpException(
            ApiUnifiedError.Http.BadRequest(TEST_ERROR_MESSAGE, HttpURLConnection.HTTP_BAD_REQUEST),
            HttpURLConnection.HTTP_BAD_REQUEST
        )
    }

    @Test
    fun `api error handler, return Generic error`() {
        testHttpException(
            ApiUnifiedError.Generic(TEST_ERROR_MESSAGE),
            HttpURLConnection.HTTP_CLIENT_TIMEOUT
        )
    }

    private fun testHttpException(expectedApiUnifiedError: ApiUnifiedError, errorCode: Int) {
        val responseBody =
            ApiErrorModel(errorMessage = TEST_ERROR_MESSAGE).toJson().toResponseBody()
        val throwable = HttpException(
            Response.error<ResponseBody>(errorCode, responseBody)
        )
        val resultApiUnifiedError = apiErrorHandlerImpl.invoke(throwable)
        assertThat(resultApiUnifiedError).isEqualTo(expectedApiUnifiedError)
    }

    private fun testIoException(
        expectedApiUnifiedError: ApiUnifiedError,
        ioException: IOException
    ) {
        val resultApiUnifiedError = apiErrorHandlerImpl.invoke(ioException)
        assertThat(resultApiUnifiedError).isEqualTo(expectedApiUnifiedError)
    }
}