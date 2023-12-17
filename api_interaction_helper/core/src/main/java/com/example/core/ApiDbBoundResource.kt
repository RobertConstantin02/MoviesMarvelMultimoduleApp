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
import com.example.core.remote.UnifiedError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <BO, DB, API> apiDbBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DatabaseResponse<DB>>, //Flow<DbResponse<DB>>
    crossinline shouldMakeNetworkRequest: suspend (DatabaseResponse<DB>) -> Boolean = { true },
    crossinline makeNetworkRequest: suspend () -> ApiResponse<API>,
    crossinline processNetworkResponse: (response: ApiResponseSuccess<API>) -> Unit = { },
    crossinline saveApiData: suspend (API) -> DatabaseResponse<Unit> = { _: API -> DatabaseResponse.create(Unit) },
    crossinline onNetworkRequestFailed: (unifiedError: UnifiedError) -> Unit = { _: UnifiedError -> },
    crossinline mapApiToDomain: (API) -> BO,
    crossinline mapLocalToDomain: (DB) -> BO,
) = flow<Resource<BO>> {
    emit(Resource.loading())

    val localData = fetchFromLocal().first()

    if (shouldMakeNetworkRequest(localData)) { //here maybe only the time, not response different from success. After one day I want to get new data and cache it
        // TODO: action to save again the time for sharedPref. Here must be something generic developer want to apply
        when (val response = makeNetworkRequest()) {
            is ApiResponseSuccess -> {
                processNetworkResponse(response)
                if (saveApiData(response.body) is DatabaseResponseSuccess)
                    fetchFromLocal().map { localResponse ->
                        when (localResponse) {
                            is DatabaseResponseSuccess -> emit(Resource.success(mapLocalToDomain(localResponse.data)))
                            is DatabaseResponseError -> emit(Resource.success(mapApiToDomain(response.body)))
                            is DatabaseResponseEmpty -> emit(Resource.success(mapApiToDomain(response.body)))
                        }
                    }
                else emit(Resource.success(mapApiToDomain(response.body)))
            }

            is ApiResponseError -> {
                onNetworkRequestFailed(response.unifiedError)
                fetchFromLocal().map { localResponse ->
                    when (localResponse) {
                        is DatabaseResponseSuccess -> emit(
                            Resource.error(
                                response.unifiedError.message,
                                mapLocalToDomain(localResponse.data)
                            )
                        )

                        is DatabaseResponseError -> emit(
                            Resource.error(
                                localResponse.databaseUnifiedError.messageResource,
                                null
                            )
                        )

                        is DatabaseResponseEmpty -> emit(Resource.successEmpty(null))
                    }
                }
            }

            is ApiResponseEmpty -> {
                fetchFromLocal().map { localResponse ->
                    when (localResponse) {
                        is DatabaseResponseSuccess -> emit(
                            Resource.successEmpty(mapLocalToDomain(localResponse.data))
                        )

                        is DatabaseResponseError -> emit(
                            Resource.error(
                                localResponse.databaseUnifiedError.messageResource,
                                null
                            )
                        )

                        is DatabaseResponseEmpty -> emit(Resource.successEmpty(null))
                    }
                }
            }
        }
    } else {
        (localData as? DatabaseResponseSuccess)?.let {
            emit(Resource.success(mapLocalToDomain(it.data)))
        } ?: emit(Resource.successEmpty(null))
    }
}
