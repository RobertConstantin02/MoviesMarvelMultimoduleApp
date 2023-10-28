package com.example.feature_favorites.paginator

interface IPaginator {
    suspend fun loadNextData()
    fun reset()
}