package com.example.test

import com.google.gson.GsonBuilder

object GsonAdapterExt {
    private val gson = GsonBuilder().create()

    internal inline fun <reified T> T.toJson(): String = gson.toJson(this)
    internal inline fun <reified T> String.fromJson(): T =
        gson.fromJson(this, T::class.java)
}