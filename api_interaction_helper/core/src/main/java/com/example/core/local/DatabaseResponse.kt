package com.example.core.local

sealed class DatabaseResponse<T> {
    fun <T> create(databaseUnifiedError: DatabaseUnifiedError) =
        DatabaseResponseError(databaseUnifiedError)

    fun <T> create(databaseResponse: T?): DatabaseResponse<out T & Any> =
        databaseResponse?.let { DatabaseResponseSuccess(databaseResponse) } ?: DatabaseResponseEmpty
}

data class DatabaseResponseSuccess<T>(val data: T): DatabaseResponse<T>()
object DatabaseResponseEmpty : DatabaseResponse<Nothing>()
data class DatabaseResponseError(val databaseUnifiedError: DatabaseUnifiedError): DatabaseResponse<DatabaseUnifiedError>()
