package com.example.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entities.CharacterEntity

@Dao
interface ICharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: ICharacterDao)

    @Query("SELECT * FROM character_entity")
    fun getAllCharacters(): PagingSource<Int, CharacterEntity>



}