package com.example.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paging_keys")
data class PagingKeys(
    @PrimaryKey
    val itemId: Long,
    val prevKey: String?,
    val nextKey: String?,
)
