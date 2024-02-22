package com.example.core.remote

import com.example.core.Resource
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

inline fun <BO, DB, API> apiDbBoundResource(
    crossinline fetchFromLocal: suspend () -> Flow<DatabaseResponse<DB>>,
    crossinline shouldMakeNetworkRequest: suspend (DatabaseResponse<DB>) -> Boolean = { true },
    crossinline localStorageStrategy: () -> Unit = {},
    crossinline makeNetworkRequest: suspend () -> ApiResponse<API>,
    crossinline processNetworkResponse: (response: ApiResponseSuccess<API>) -> Unit = { },
    crossinline saveApiData: suspend (API) -> DatabaseResponse<Unit> = { _: API -> DatabaseResponse.create(Unit) },
    crossinline onNetworkRequestFailed: (apiUnifiedError: ApiUnifiedError) -> Unit = { _: ApiUnifiedError -> },
    crossinline mapApiToDomain: (API) -> BO,
    crossinline mapLocalToDomain: (DB) -> BO,
) = flow<Resource<BO>> {
    val localData = fetchFromLocal().first()
    //if is success and cache condition is not satisfied also satisfy condition.
    if (shouldMakeNetworkRequest(localData)) { //here maybe only the time, not response different from success. After one day I want to get new data and cache it
        when (val response = makeNetworkRequest()) {
            is ApiResponseSuccess -> {
                //for example if me made an api call and is success with data, maybe want to cache
                // the time in which that request was made to handle when we want to ask server again.
                // Here save time, then in shouldMakeNetworkRequest call checks condition. if satisfied make
                // the request and if that  is success then save again the current time.
                localStorageStrategy()
                processNetworkResponse(response)
                if (saveApiData(response.body) is DatabaseResponseSuccess)
                    when (val localResponse = fetchFromLocal().first()) {
                        is DatabaseResponseSuccess -> {
                            emit(Resource.success(mapLocalToDomain(localResponse.data)))
                        }

                        is DatabaseResponseError -> {
                            emit(Resource.success(mapApiToDomain(response.body)))
                        }

                        is DatabaseResponseEmpty -> {
                            emit(Resource.success(mapApiToDomain(response.body)))
                        }
                    }
                else {
                    emit(Resource.success(mapApiToDomain(response.body)))
                }
            }

            is ApiResponseError -> {
                onNetworkRequestFailed(response.apiUnifiedError)
                when (val localResponse = fetchFromLocal().first()) {
                    is DatabaseResponseSuccess -> {
                        emit(Resource.error(
                                response.apiUnifiedError,
                                mapLocalToDomain(localResponse.data)))
                    }

                    is DatabaseResponseError -> {
                        emit(Resource.error(response.apiUnifiedError, null))
                    }

                    is DatabaseResponseEmpty -> {
                        emit(Resource.error(response.apiUnifiedError, null))
                    }
                }
            }
            //no fetch from data base. If is empty and Id need to refresh local database,
            // then I wont represent not updated data to user
            is ApiResponseEmpty -> {
                emit(Resource.successEmpty())
            }
        }
    } else {
        (localData as? DatabaseResponseSuccess)?.let {
            emit(Resource.success(mapLocalToDomain(it.data)))
        }
    }
}