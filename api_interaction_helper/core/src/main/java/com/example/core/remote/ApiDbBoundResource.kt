package com.example.core.remote

import android.util.Log
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
    crossinline localStorageStrategy: () -> Unit = {}, //if we applyed local strateguCache or not
    crossinline makeNetworkRequest: suspend () -> ApiResponse<API>,
    crossinline processNetworkResponse: (response: ApiResponseSuccess<API>) -> Unit = { },
    crossinline saveApiData: suspend (API) -> DatabaseResponse<Unit> = { _: API ->
        DatabaseResponse.create(
            Unit
        )
    },
    crossinline onNetworkRequestFailed: (unifiedError: UnifiedError) -> Unit = { _: UnifiedError -> },
    crossinline mapApiToDomain: (API) -> BO,
    crossinline mapLocalToDomain: (DB) -> BO,
) = flow<Resource<BO>> {
    val localData = fetchFromLocal().first()
    //if is success and you cache condition is not satisfied also enter.
    if (shouldMakeNetworkRequest(localData)) { //here maybe only the time, not response different from success. After one day I want to get new data and cache it
        when (val response = makeNetworkRequest()) {
            is ApiResponseSuccess -> {
                localStorageStrategy() //for example if me made an api call and is success with data, we maybe want to cache the time in wich that request was made to handle when we want to ask server again. Hre save time, then in shouldmekeNetwork call check condiition. if satfied make the reqeust and if taht is succeess then save again the current time.
                processNetworkResponse(response)
                if (saveApiData(response.body) is DatabaseResponseSuccess)
                    when (val localResponse = fetchFromLocal().first()) {
                        is DatabaseResponseSuccess -> {
                            //*1
                            println("-----> 1 api success, local success -> @return  Resource.success with local data: ${localResponse.data}")
                            emit(
                                Resource.success(
                                    mapLocalToDomain(localResponse.data)
                                )
                            )
                        }

                        is DatabaseResponseError -> {
                            //*2
                            println("-----> 2 api success, local error -> @return  Resource.success with api data: ${response.body}")
                            emit(
                                Resource.success(
                                    mapApiToDomain(
                                        response.body
                                    )
                                )
                            )
                        }

                        is DatabaseResponseEmpty -> {
                            //*3
                            println("-----> 3 api success, local empty -> @return  Resource.success with api data: ${response.body}")
                            emit(
                                Resource.success(
                                    mapApiToDomain(response.body)
                                )
                            )
                        }
                    }
                else {
                    println("-----> 4 api success, save local error -> @return  Resource.success with api data: ${response.body}")
                    emit(Resource.success(mapApiToDomain(response.body)))
                }
            }

            is ApiResponseError -> {
                onNetworkRequestFailed(response.unifiedError)
                when (val localResponse = fetchFromLocal().first()) {
                    is DatabaseResponseSuccess -> {
                        //*5
                        println("-----> 5 api error, local success with previous saved data -> @return  Resource.error with api error message:  ${response.unifiedError.message} and local data ${localResponse.data}")
                        emit(
                            Resource.error(
                                response.unifiedError.message,
                                mapLocalToDomain(localResponse.data)
                            )
                        )
                    }

                    is DatabaseResponseError -> {
                        //*6
                        println("-----> 6 api error, local error -> @return  Resource.error with api error message:  ${response.unifiedError.message} and local data: null")
                        emit(
                            Resource.error(
                                response.unifiedError.message,
                                null
                            )
                        )
                    }

                    is DatabaseResponseEmpty -> {
                        //*7
                        println("-----> 7 api error, local error -> @return  Resource.error with api error message:  ${response.unifiedError.message} and local data: null")
                        emit(
                            Resource.error(
                                response.unifiedError.message,
                                null
                            )
                        )
                    }
                }
            }

            is ApiResponseEmpty -> {
                println("-----> 8 api empty -> @return  Resource.successEmpty")
                emit(Resource.successEmpty())
            } //no fetch from data base. If is empty and we need to refresh local database, then We wont represent not updated data to user
        }
    } else {
        (localData as? DatabaseResponseSuccess)?.let {
            println("-----> 9 local success -> @returns local data: ${it.data}")
            emit(Resource.success(mapLocalToDomain(it.data)))
        }
    }
}