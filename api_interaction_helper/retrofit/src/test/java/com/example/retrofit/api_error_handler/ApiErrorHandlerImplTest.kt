package com.example.retrofit.api_error_handler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.core.R
import com.example.core.remote.UnifiedError
import okhttp3.ResponseBody
import okhttp3.internal.EMPTY_RESPONSE
import org.junit.Before

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowApplication
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val TEST_ERROR_MESSAGE = "Error Message"

@RunWith(RobolectricTestRunner::class)
@Suppress("IllegalIdentifier")
class ApiErrorHandlerImplTest{
    private lateinit var apiErrorHandlerImpl: ApiErrorHandlerImpl
    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        apiErrorHandlerImpl = ApiErrorHandlerImpl(context)
    }

    /**
     * IOException is implemented by different classes like SocketTimeoutException.
     * Throwable is parent class of Exception which in turn is parent of IOException
     * invoke(t: Throwable) -> then SocketTimeoutException() as parameter
     */
    @Test
    fun `api error handler, return IOException SocketTimeoutException`() {
        val currentError = UnifiedError.Connectivity.TimeOut(message = context.getString(R.string.error_time_out))

        val expectedError = apiErrorHandlerImpl.invoke(SocketTimeoutException())

        assertThat(expectedError).isEqualTo(currentError)
    }

    @Test
    fun `api error handler, return IOException ConnectException`() {
        val currentError = UnifiedError.Connectivity.NoConnection(message = context.getString(R.string.error_network_connection))

        val expectedError = apiErrorHandlerImpl.invoke(ConnectException())

        assertThat(expectedError).isEqualTo(currentError)
    }

    @Test
    fun `api error handler, return IOException UnknownHostException`() {
        val currentError = UnifiedError.Connectivity.HostUnreachable(context.getString(R.string.error_generic))

        val expectedError = apiErrorHandlerImpl.invoke(UnknownHostException())

        assertThat(expectedError).isEqualTo(currentError)
    }

    @Test
    fun `api error handler, return IOException Generic`() {
        val currentError = UnifiedError.Generic(context.getString(R.string.error_generic))

        val expectedError = apiErrorHandlerImpl.invoke(ProtocolException())

        assertThat(expectedError).isEqualTo(currentError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_UNAUTHORIZED`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_UNAUTHORIZED, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Http.Unauthorized(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_NOT_FOUND`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NOT_FOUND, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Http.NotFound(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_INTERNAL_ERROR`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_INTERNAL_ERROR, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Http.InternalError(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_BAD_REQUEST`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_BAD_REQUEST, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Http.BadRequest(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_NO_CONTENT`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NO_CONTENT, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Http.EmptyResponse(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }

    @Test
    fun `api error handler, return HttpException HTTP_NOT_ACCEPTABLE`() {
        val currentError = HttpException(Response.error<ResponseBody>(HttpURLConnection.HTTP_NOT_ACCEPTABLE, EMPTY_RESPONSE))

        val expectedUnifiedError = UnifiedError.Generic(message = TEST_ERROR_MESSAGE)

        val unifiedError = apiErrorHandlerImpl.invoke(currentError)

        assertThat(unifiedError).isEqualTo(expectedUnifiedError)
    }
}