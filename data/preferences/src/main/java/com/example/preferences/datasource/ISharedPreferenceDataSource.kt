package com.example.preferences.datasource

interface ISharedPreferenceDataSource {
    fun saveCurrentTimeMs()
    fun getTime(): Long
}