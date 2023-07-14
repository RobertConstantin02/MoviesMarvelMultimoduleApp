package com.example.heroes_data.db.detasource

import androidx.paging.PagingSource
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys

interface ICharacterLocalDatasource {
    fun getAllCharacters(): PagingSource<Int, CharacterEntity>
    suspend fun getPagingKeysById(id: Long): PagingKeys?
}