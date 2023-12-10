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
            else -> UnifiedError.Generic(message = message)
        }
    }

    private fun IOException.handleError(): UnifiedError =
        when (this) {
            is SocketTimeoutException -> UnifiedError.Connectivity.TimeOut(context.getString(R.string.error_time_out))

            is ConnectException -> UnifiedError.Connectivity.NoConnection(context.getString(R.string.error_network_connection))

            is UnknownHostException -> UnifiedError.Connectivity.HostUnreachable(context.getString(R.string.error_generic))

            else -> UnifiedError.Generic(context.getString(R.string.error_generic))
        }
}