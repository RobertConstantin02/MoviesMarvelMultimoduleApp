package com.example.core.local

sealed class DatabaseResponse<T> {
    companion object {
        fun <T> create(localUnifiedError: LocalUnifiedError): DatabaseResponseError<T> =
            DatabaseResponseError(localUnifiedError)

        fun <T> create(databaseResponse: T?): DatabaseResponse<T> =
            databaseResponse?.let {
                DatabaseResponseSuccess(databaseResponse)
            } ?: run {
                DatabaseResponseEmpty()
            }
    }
}

data class DatabaseResponseSuccess<T>(val data: T): DatabaseResponse<T>()
class DatabaseResponseEmpty<T> : DatabaseResponse<T>()
data class DatabaseResponseError<T> (val localUnifiedError: LocalUnifiedError): DatabaseResponse<T>()
