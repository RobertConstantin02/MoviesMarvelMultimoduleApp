package com.example.retrofit.api_error_handler

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.core.R
import com.example.core.remote.ApiUnifiedError
import okhttp3.ResponseBody
import okhttp3.internal.EMPTY_RESPONSE

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val TEST_ERROR_MESSAGE = "Error Message"

class ApiErrorHandlerImplTest{
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
        val expectedError = ApiUnifiedError.Connectivity.TimeOut("Error Test")

        val resultError = apiErrorHandlerImpl.invoke(SocketTimeoutException("Error Test"))

        assertThat(resultError).isEqualTo(expectedError)
    }

    @Test
    fun `api error handler, return IOException ConnectException`() {
        val expectedError = ApiUnifiedError.Connectivity.NoConnection("Error Test")

        val resultError = apiErrorHandlerImpl.invoke(ConnectException("Error Test"))

        assertThat(resultError).isEqualTo(expectedError)
    }

    @Test
    fun `api error handler, return IOException UnknownHostException`() {
        val expectedError = ApiUnifiedError.Connectivity.HostUnreachable("Error Test")

        val resultError = apiErrorHandlerImpl.invoke(UnknownHostException("Error Test"))

        assertThat(resultError).isEqualTo(expectedError)
    }

    @Test
    fun `api error handler, return IOException Generic`() {
        val expectedError = ApiUnifiedError.Generic("Error Test")

        val resultError = apiErrorHandlerImpl.invoke(ProtocolException("Error Test"))

        assertThat(resultError).isEqualTo(expectedError)
    }

//    @Test
//    fun `api error handler, return HttpException HTTP_UNAUTHORIZED`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_UNAUTHORIZED, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Http.Unauthorized()
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
//
//    @Test
//    fun `api error handler, return HttpException HTTP_NOT_FOUND`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NOT_FOUND, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Http.NotFound()
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
//
//    @Test
//    fun `api error handler, return HttpException HTTP_INTERNAL_ERROR`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_INTERNAL_ERROR, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Http.InternalErrorApi()
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
//
//    @Test
//    fun `api error handler, return HttpException HTTP_BAD_REQUEST`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_BAD_REQUEST, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Http.BadRequest()
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
//
//    @Test
//    fun `api error handler, return HttpException HTTP_NO_CONTENT`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NO_CONTENT, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Http.EmptyResponse()
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
//
//    @Test
//    fun `api error handler, return HttpException HTTP_NOT_ACCEPTABLE`() {
//        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NOT_ACCEPTABLE, EMPTY_RESPONSE))
//
//        val expectedApiUnifiedError = ApiUnifiedError.Generic(message = TEST_ERROR_MESSAGE)
//
//        val unifiedError = apiErrorHandlerImpl.invoke(currentError)
//
//        assertThat(unifiedError).isEqualTo(expectedApiUnifiedError)
//    }
}