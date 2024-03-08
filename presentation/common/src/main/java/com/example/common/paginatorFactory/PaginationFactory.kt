package com.example.common.paginatorFactory

interface Configuration
abstract class PaginationFactory<T: Configuration> {
    abstract fun createPagination(configuration: T): IPaginator
}
