package com.example.retrofit.di

import com.example.retrofit.adapter.CallResult
import dagger.assisted.AssistedFactory
import retrofit2.Call

@AssistedFactory
internal interface ICallResultFactory {
    fun<T> create(proxy: Call<T>): CallResult<T>
}