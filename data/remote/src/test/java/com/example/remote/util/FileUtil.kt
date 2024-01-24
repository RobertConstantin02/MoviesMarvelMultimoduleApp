package com.example.remote.util

object FileUtil {
    fun getJson(file: String) = javaClass.classLoader?.getResource(file)?.readText()
}