package com.example.test

object FileUtil {
    fun getJson(file: String) =
        javaClass.classLoader?.getResource(file)?.readText()
}