package com.example.retrofit.api_error_handler

import android.content.Context
import com.example.core.remote.UnifiedError
import com.example.core.implement.IApiErrorHandler
import com.example.core.R
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * HttpException and IOException are both exceptions in Java (and Kotlin) that can
 * occur during network operations, but they represent different types of errors.
 *
 * @IOException : IOException is a general exception class in Java that is a subclass of Exception.
 * It is thrown when an I/O operation (input/output) encounters an unexpected problem.
 * I/O operations include reading from or writing to streams, files, sockets, etc.
 * In the context of networking, IOException can be thrown for various reasons, such as
 * network issues, connection timeouts, or problems with reading or writing data.
 *
 * @HttpException : HttpException is a class specific to the Retrofit library, which is commonly
 * used for making HTTP requests in Android applications.
 * HttpException is thrown when an HTTP response indicates a failure, i.e., when the HTTP
 * status code is in the range 400-599 (client or server errors).
 */
class ApiErrorHandlerImpl(
    @ApplicationContext private val context: Context
) : IApiErrorHandler {
    override fun invoke(t: Throwable): UnifiedError =
        when (t) {
            is IOException -> t.handleError()
            is HttpException -> t.handleError()
            else -> UnifiedError.Generic(context.getString(R.string.error_generic))
        }

    private fun HttpException.handleError(): UnifiedError {
        val message = response()?.errorBody()?.string() ?: message()
        return when (code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> UnifiedError.Http.Unauthorized(message = message)
                HttpURLConnection.HTTP_NOT_FOUND -> UnifiedError.Http.NotFound(message = message)
                HttpURLConnection.HTTP_INTERNAL_ERROR -> UnifiedError.Http.InternalError(message = message)
                HttpURLConnection.HTTP_BAD_REQUEST -> UnifiedError.Http.BadRequest(message = message)
                HttpURLConnection.HTTP_NO_CONTENT -> UnifiedError.Http.EmptyResponse(message = message)
                else -> UnifiedError.Generic(context.getString(R.string.error_generic))
            }
        }

    private fun IOException.handleError(): UnifiedError =
        when (this) {
            is SocketTimeoutException -> UnifiedError.Connectivity.TimeOut(context.getString(R.string.error_time_out)) //maybe here change for message from Exception or leave it with int becaus enow Resource.error handles it

            is ConnectException -> UnifiedError.Connectivity.NoConnection(context.getString(R.string.error_network_connection))

            is UnknownHostException -> UnifiedError.Connectivity.HostUnreachable(context.getString(R.string.error_generic))

            else -> UnifiedError.Generic(context.getString(R.string.error_generic))
        }
}
