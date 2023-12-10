package com.example.core

import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.remote.ApiResponse
import com.example.core.remote.ApiResponseEmpty
import com.example.core.remote.ApiResponseError
import com.example.core.remote.ApiResponseSuccess
import com.example.core.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <DB, API> apiDbBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DatabaseResponse<DB>>, //Flow<DbResponse<DB>>
    crossinline shouldMakeNetworkRequest: suspend () -> Boolean = { true },
    crossinline makeNetworkRequest: suspend () -> ApiResponse<API>,
    crossinline processNetworkResponse: (response: ApiResponseSuccess<API>) -> Unit = { },
    crossinline saveResponseData: suspend (API) -> Unit = { },
    crossinline onNetworkRequestFailed: (errorMessage: String) -> Unit = { _: String -> }
) = flow<Resource<DB>> {
    emit(Resource.loading())

    if (shouldMakeNetworkRequest()) {
        when (val response = makeNetworkRequest()) {
            is ApiResponseSuccess -> {
                processNetworkResponse(response)
                saveResponseData(response.body)
                fetchFromLocal().map { localData ->
                    when (localData) {
                        is DatabaseResponseSuccess -> emit(Resource.success(localData.data))
                        is DatabaseResponseError -> emit(Resource.success(response.body)) //maybe a funciton to map. an other crossinline that maps
                        is DatabaseResponseEmpty -> Resource.successEmpty()
                    }
                }

            }

            is ApiResponseError -> {
                fetchFromLocal().map { localData ->
                    when (localData) {
                        is DatabaseResponseSuccess -> emit(Resource.success(localData.data))
                        is DatabaseResponseError -> emit(Resource.error(localData.databaseUnifiedError.message))
                        is DatabaseResponseEmpty -> emit(Resource.error(localData.databaseUnifiedError.message))
                    }
                }
            }
            is ApiResponseEmpty -> {
                fetchFromLocal().map { localData ->
                    when (localData) {
                        is DatabaseResponseSuccess -> emit(Resource.success(localData.data))
                        is DatabaseResponseError -> emit(Resource.error(localData.databaseUnifiedError.message))
                        is DatabaseResponseEmpty -> emit(Resource.error(localData.databaseUnifiedError.message))
                    }
                }
            }
        }
    } else {
        when (val localData = fetchFromLocal().first()) {
            is DatabaseResponseSuccess -> TODO()
            is DatabaseResponseError -> TODO()
            is DatabaseResponseEmpty -> TODO()
        }
    }

}

fun <T> FlowCollector<Resource<T>>.handleNetworkRequest() =

