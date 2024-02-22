package com.example.test.util

object FileUtil {
    fun getJson(file: String) =
        javaClass.classLoader?.getResource(file)?.readText()
}