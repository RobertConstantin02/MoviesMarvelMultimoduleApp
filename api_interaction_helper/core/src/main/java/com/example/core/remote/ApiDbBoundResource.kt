package com.example.core.remote

import com.example.core.Resource
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <BO, DB, API> apiDbBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DatabaseResponse<DB>>, //Flow<DbResponse<DB>>
    crossinline shouldMakeNetworkRequest: suspend (DatabaseResponse<DB>) -> Boolean = { true },
    crossinline localStorageStrategy: () -> Unit = {},
    crossinline makeNetworkRequest: suspend () -> ApiResponse<API>,
    crossinline processNetworkResponse: (response: ApiResponseSuccess<API>) -> Unit = { },
    crossinline saveApiData: suspend (API) -> DatabaseResponse<Unit> = { _: API -> DatabaseResponse.create(Unit) },
    crossinline onNetworkRequestFailed: (unifiedError: UnifiedError) -> Unit = { _: UnifiedError -> },
    crossinline mapApiToDomain: (API) -> BO,
    crossinline mapLocalToDomain: (DB) -> BO,
) = flow<Resource<BO>> {
    val localData = fetchFromLocal().first()

    if (shouldMakeNetworkRequest(localData)) { //here maybe only the time, not response different from success. After one day I want to get new data and cache it
        localStorageStrategy()
        when (val response = makeNetworkRequest()) {
            is ApiResponseSuccess -> {
                processNetworkResponse(response)
                if (saveApiData(response.body) is DatabaseResponseSuccess)
                    when (val localResponse = fetchFromLocal().first()) {
                        is DatabaseResponseSuccess -> //*1
                            emit(
                                Resource.success(
                                    mapLocalToDomain(
                                        localResponse.data
                                    )
                                )
                            )

                        is DatabaseResponseError -> //*2
                            emit(
                                Resource.success(
                                    mapApiToDomain(
                                        response.body
                                    )
                                )
                            )

                        is DatabaseResponseEmpty -> //*3
                            emit(
                                Resource.success(
                                    mapApiToDomain(
                                        response.body
                                    )
                                )
                            )
                    }
                else emit(Resource.success(mapApiToDomain(response.body))) //*4
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

                        is DatabaseResponseEmpty -> emit(Resource.successEmpty())
                    }
                }
            }

            is ApiResponseEmpty -> Resource.successEmpty() //no fetch from data base. If is empty and we need to refresh local database, then We wont represent not updated data to user
        }
    } else {
        (localData as? DatabaseResponseSuccess)?.let {
            emit(Resource.success(mapLocalToDomain(it.data)))
        }
            ?: emit(Resource.successEmpty())
    }
}