package com.example.preferences

import android.content.SharedPreferences
import com.example.preferences.ICacheSystem.Companion.KEY_CURRENT_TIME
import javax.inject.Inject

class CacheManagerSystem @Inject constructor(private val sharedPref: SharedPreferences): ICacheSystem {

    override fun saveCurrentTimeMs() {
        sharedPref.edit().putLong(KEY_CURRENT_TIME, System.currentTimeMillis()).apply()
    }

    override fun getTime() = sharedPref.getLong(KEY_CURRENT_TIME, 0L)

}