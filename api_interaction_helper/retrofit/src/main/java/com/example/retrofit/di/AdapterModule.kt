package com.example.retrofit.di

import android.content.Context
import com.example.core.implement.IApiErrorHandler
import com.example.retrofit.api_error_handler.ApiErrorHandlerImpl
import com.example.retrofit.facotry.CallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AdapterModule {

    @Provides
    @Singleton
    fun provideCallAdapterFactory(apiErrorHandler: IApiErrorHandler): CallAdapterFactory =
         CallAdapterFactory(apiErrorHandler)


    @Provides
    @Singleton
    fun provideApiErrorHandler(@ApplicationContext context: Context) : IApiErrorHandler =
        ApiErrorHandlerImpl(context)



//    @Provides
//    @Singleton
//    fun provideCallResultFactory(apiErrorHandler: IApiErrorHandler): ICallResultFactory =
//        CallResultFactoryImpl(apiErrorHandler)
}

//internal class CallResultFactoryImpl @Inject constructor(
//    private val apiErrorHandler: IApiErrorHandler
//) : ICallResultFactory {
//
//    override fun <T> create(proxy: Call<T>): CallResult<T> =
//        CallResult(proxy, apiErrorHandler)
//}