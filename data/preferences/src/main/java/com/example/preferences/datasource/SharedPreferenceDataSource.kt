package com.example.preferences.datasource

import com.example.preferences.ICacheSystem
import javax.inject.Inject
class SharedPreferenceDataSource @Inject constructor(private val sharedPref: ICacheSystem): ISharedPreferenceDataSource {
    override fun saveCurrentTimeMs() = sharedPref.saveCurrentTimeMs()

    override fun getTime(): Long = sharedPref.getTime()
}