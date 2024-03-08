package com.example.common.paginatorFactory

interface IPaginator {
    suspend fun loadNextData()
    fun reset()
    fun stopCollection()
}