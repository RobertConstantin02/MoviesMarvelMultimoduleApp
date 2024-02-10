package com.example.retrofit.api_error_handler

import com.example.core.implement.IApiErrorHandler
import com.example.core.remote.ApiUnifiedError
import com.google.gson.Gson
import com.google.gson.JsonIOException
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
class ApiErrorHandlerImpl : IApiErrorHandler {
    override fun invoke(t: Throwable): ApiUnifiedError =
        when (t) {
            is IOException -> t.handleError()
            is HttpException -> t.handleError()
            else -> ApiUnifiedError.Generic(t.message)
        }

    private fun HttpException.handleError(): ApiUnifiedError {
        val apiError = response()?.errorBody()?.string()

        val apiErrorMessage = try {
            Gson().fromJson(apiError, ApiErrorModel::class.java).errorMessage
        }catch (e: JsonIOException) {
            ""
        }.ifEmpty { message() }

        return when (val code = code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> ApiUnifiedError.Http.Unauthorized(message = apiErrorMessage, code)
                HttpURLConnection.HTTP_NOT_FOUND -> ApiUnifiedError.Http.NotFound(message = apiErrorMessage, code)
                HttpURLConnection.HTTP_INTERNAL_ERROR -> ApiUnifiedError.Http.InternalErrorApi(message = apiErrorMessage, code)
                HttpURLConnection.HTTP_BAD_REQUEST -> ApiUnifiedError.Http.BadRequest(message = apiErrorMessage, code)
                HttpURLConnection.HTTP_NO_CONTENT -> ApiUnifiedError.Http.EmptyResponse(message = apiErrorMessage, code)
                else -> ApiUnifiedError.Generic(apiErrorMessage)
            }
        }

    private fun IOException.handleError(): ApiUnifiedError =
        when (this) {
            is SocketTimeoutException -> ApiUnifiedError.Connectivity.TimeOut(message)
            is ConnectException -> ApiUnifiedError.Connectivity.NoConnection(message)
            is UnknownHostException -> ApiUnifiedError.Connectivity.HostUnreachable(message)
            else -> ApiUnifiedError.Generic(message)
        }
}
