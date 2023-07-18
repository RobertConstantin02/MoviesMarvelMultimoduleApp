package com.example.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.PagingKeys

@Dao
interface IPagingKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<PagingKeys>)

    @Query("SELECT * FROM paging_keys WHERE itemId = :itemId")
    suspend fun getPagingKeysById(itemId: Long): PagingKeys?

    @Query("DELETE FROM paging_keys")
    suspend fun clearPagingKeys()
}