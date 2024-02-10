package com.example.test

import com.google.gson.GsonBuilder

object GsonAdapterExt {
    val gson = GsonBuilder().create()

    inline fun <reified T> T.toJson(): String = gson.toJson(this)
    inline fun <reified T> String.fromJson(): T =
        gson.fromJson(this, T::class.java)
}