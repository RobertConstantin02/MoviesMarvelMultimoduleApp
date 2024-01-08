package com.example.domain_model.error

sealed class DomainError<T> {
    data class ApiError<T>(val message: String, val data: T?): DomainError<T>()
    data class LocalError<T>(val error: Int): DomainError<T>()

}