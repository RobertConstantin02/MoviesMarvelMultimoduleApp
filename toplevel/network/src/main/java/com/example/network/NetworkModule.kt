package com.example.network


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        val timeout: Long = 240
        return OkHttpClient()
            .newBuilder()
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideService(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com")// why buildconfig from this module does not appear?
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

//    @Provides
//    fun provideRickMortyEndPoint(
//        retrofitBuilder: Retrofit.Builder,
//        @Named(rickMortyBaseUrl) baseUrl: String
//    ): RickMortyEndPoint {
//        val retrofit = retrofitBuilder.baseUrl(baseUrl).build()
//        return retrofit.create(RickMortyEndPoint::class.java)
//    }
}
