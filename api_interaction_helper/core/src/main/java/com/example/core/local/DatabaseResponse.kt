package com.example.core.local

sealed class DatabaseResponse<T> {
    companion object {
        fun <T> create(databaseUnifiedError: DatabaseUnifiedError): DatabaseResponseError<T> =
            DatabaseResponseError(databaseUnifiedError)

        fun <T> create(databaseResponse: T?): DatabaseResponse<T> =
            databaseResponse?.let { DatabaseResponseSuccess(databaseResponse) } ?: DatabaseResponseEmpty()
    }

}

data class DatabaseResponseSuccess<T>(val data: T): DatabaseResponse<T>()
class DatabaseResponseEmpty<T> : DatabaseResponse<T>()
data class DatabaseResponseError<T> (val databaseUnifiedError: DatabaseUnifiedError): DatabaseResponse<T>()
