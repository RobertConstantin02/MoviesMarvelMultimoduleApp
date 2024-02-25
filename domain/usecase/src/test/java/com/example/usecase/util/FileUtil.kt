package com.example.usecase.util

object FileUtil {
    fun getJson(file: String) =
        javaClass.classLoader?.getResource(file)?.readText()
}