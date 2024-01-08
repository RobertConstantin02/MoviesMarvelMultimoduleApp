package com.example.core.implement

interface Response <T> {
    val isSuccessful: Boolean
    val code: Int
    val errorDescription: String
    fun body(): T?
    fun headers(): Set<Map.Entry<String, List<String>>>
}