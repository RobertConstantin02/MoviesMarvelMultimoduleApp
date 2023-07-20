package com.example.heroes_data.db.detasource

import androidx.paging.PagingSource
import com.example.database.dao.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import javax.inject.Inject

class CharacterLocalDatasource @Inject constructor(
    private val characterDao:  ICharacterDao,
    private val pagingKeysDao: IPagingKeysDao,
): ICharacterLocalDatasource {

    override fun getAllCharacters(): PagingSource<Int, CharacterEntity> =
        characterDao.getAllCharacters()

    override suspend fun getCharacterById(characterId: Int): CharacterEntity? = characterDao.getCharacterById(characterId)

    override suspend fun getPagingKeysById(id: Long): PagingKeys? = pagingKeysDao.getPagingKeysById(id)

    override suspend fun insertPagingKeys(keys: List<PagingKeys>) = pagingKeysDao.insertAll(keys)

    override suspend fun insertCharacters(characters: List<CharacterEntity>) = characterDao.insertCharacters(characters)


}