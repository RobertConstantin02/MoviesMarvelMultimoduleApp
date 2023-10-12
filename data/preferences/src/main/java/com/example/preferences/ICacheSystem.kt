package com.example.preferences

interface ICacheSystem {
    fun saveCurrentTimeMs()
    fun getTime(): Long
    companion object {
        const val KEY_CURRENT_TIME = "current_time"
    }
}