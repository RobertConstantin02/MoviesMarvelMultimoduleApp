package com.example.database.detasource.character

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseUnifiedError
import com.example.database.dao.IPagingKeysDao
import com.example.database.dao.character.ICharacterDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// TODO: Refactor with database error

class CharacterLocalDatasource @Inject constructor(
    private val characterDao: ICharacterDao,
    private val pagingKeysDao: IPagingKeysDao,
) : ICharacterLocalDatasource {

    override fun getAllCharacters(): PagingSource<Int, CharacterEntity> =
        characterDao.getAllCharacters()

    override suspend fun getCharacterById(characterId: Int): Flow<DatabaseResponse<CharacterEntity>> =
        flow {
            try {
                with(characterDao.getCharacterById(characterId)) {
                    emit(DatabaseResponse.create(this))
                }
            } catch (e: SQLiteException) {
                emit(DatabaseResponse.create(DatabaseUnifiedError.Reading))
            }
        }


    override suspend fun getCharactersByIds(characterIds: List<Int>): Flow<DatabaseResponse<List<CharacterEntity>>> =
        flow {
            try {
                with(characterDao.getCharactersByIds(characterIds)) {
                    emit(DatabaseResponse.create(this))
                }
            } catch (e: SQLiteException) {
                emit(DatabaseResponse.create(DatabaseUnifiedError.Reading))
            }
        }


    override suspend fun getPagingKeysById(id: Long): PagingKeys? =
        pagingKeysDao.getPagingKeysById(id)

    override suspend fun insertPagingKeys(keys: List<PagingKeys>) = pagingKeysDao.insertAll(keys)

    override suspend fun insertCharacters(characters: List<CharacterEntity>): DatabaseResponse<Unit> =
        try {
            with(characterDao.insertCharacters(*characters.toTypedArray())) {
                if (this.size == characters.size) DatabaseResponse.create(Unit)
                else DatabaseResponse.create(DatabaseUnifiedError.Insertion)
            }
        } catch (e: SQLiteException) {
            DatabaseResponse.create(DatabaseUnifiedError.Reading)
        }


    override suspend fun insertCharacter(character: CharacterEntity): DatabaseResponse<Unit> =
        try {
            if (characterDao.insertCharacter(character) != -1L) DatabaseResponse.create(Unit)
            else DatabaseResponse.create(DatabaseUnifiedError.Insertion)
        } catch (e: SQLiteException) {
            DatabaseResponse.create(DatabaseUnifiedError.Reading)
        }


    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ) = flow {
        try {
            if (characterDao.updateCharacterIsFavorite(isFavorite, characterId) != -1)
                emit(DatabaseResponse.create(Unit))
            else emit(DatabaseResponse.create(DatabaseUnifiedError.Update))
        } catch (e: SQLiteException) {
            emit(DatabaseResponse.create(DatabaseUnifiedError.Reading))
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavoriteCharacters(offset: Int): Flow<DatabaseResponse<List<CharacterEntity>>> {
        return try {
            characterDao.getFavoriteCharacters(offset).flatMapLatest { characters ->
                if (characters.isNotEmpty()) flowOf(DatabaseResponse.create(characters))
                else flowOf(DatabaseResponseEmpty())
            }
        } catch (e: SQLiteException) {
            flowOf(DatabaseResponse.create(DatabaseUnifiedError.Reading))
        }
    }
}