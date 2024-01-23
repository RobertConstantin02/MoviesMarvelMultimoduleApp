package com.example.remote.extension

import com.example.api.network.RickAndMortyService
import com.example.core.implement.IApiErrorHandler
import com.example.remote.di.NetworkModule.provideInterceptor
import com.example.remote.di.NetworkModule.provideOkHttpClient
import com.example.remote.di.NetworkModule.provideRickMortyService
import com.example.remote.di.NetworkModule.provideService
import com.example.retrofit.di.AdapterModule_ProvideCallAdapterFactoryFactory.provideCallAdapterFactory
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.toRickAndMortyService(
    apiErrorHandler: IApiErrorHandler
): RickAndMortyService {
    val retrofit = provideService(
        baseUrl = url("").toString(),
        client = provideOkHttpClient(interceptor = provideInterceptor()),
        callAdapterFactory = provideCallAdapterFactory(apiErrorHandler)
    )
    return provideRickMortyService(retrofit)
}