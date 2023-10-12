package com.example.preferences.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.preferences.CacheManagerSystem
import com.example.preferences.ICacheSystem
import com.example.preferences.datasource.ISharedPreferenceDataSource
import com.example.preferences.datasource.SharedPreferenceDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [PreferenceModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @InstallIn(SingletonComponent::class)
    @Module
    interface Declarations {
        @Singleton
        @Binds
        fun bindsPreferencesDataSource(implementation: SharedPreferenceDataSource): ISharedPreferenceDataSource

        @Singleton
        @Binds
        fun bindsCacheManagerSystem(implementation: CacheManagerSystem): ICacheSystem
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        return app.getSharedPreferences("shared_pref", MODE_PRIVATE)
    }
}