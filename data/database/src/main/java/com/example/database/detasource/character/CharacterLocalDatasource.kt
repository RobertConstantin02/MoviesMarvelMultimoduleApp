package com.example.database.detasource.character

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import arrow.core.left
import arrow.core.right
import com.example.database.dao.character.ICharacterDao
import com.example.database.dao.IPagingKeysDao
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import com.example.resources.DataBase
import com.example.resources.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// TODO: Refactor with database error

class CharacterLocalDatasource @Inject constructor(
    private val characterDao: ICharacterDao,
    private val pagingKeysDao: IPagingKeysDao,
) : ICharacterLocalDatasource {

    override fun getAllCharacters(): PagingSource<Int, CharacterEntity> =
        characterDao.getAllCharacters()

    override suspend fun getCharacterById(characterId: Int): Result<CharacterEntity> =
        with(characterDao.getCharacterById(characterId)) {
            this?.right() ?: DataBase.EmptyResult.left()
        }

    override suspend fun getCharactersByIds(characterIds: List<Int>): Result<List<CharacterEntity>> =
        with(characterDao.getCharactersByIds(characterIds)) {
            if (isNullOrEmpty()) DataBase.EmptyResult.left() else this.right()
        }

    override suspend fun getPagingKeysById(id: Long): PagingKeys? =
        pagingKeysDao.getPagingKeysById(id)

    override suspend fun insertPagingKeys(keys: List<PagingKeys>) = pagingKeysDao.insertAll(keys)

    override suspend fun insertCharacters(characters: List<CharacterEntity>): Result<Unit> =
        with(characterDao.insertCharacters(*characters.toTypedArray())) {
            if (this.size == characters.size) Unit.right()
            else DataBase.Error.Insertion.left()
        }

    override suspend fun insertCharacter(character: CharacterEntity) =
        if (characterDao.insertCharacter(character) != -1L) Unit.right()
        else DataBase.Error.Insertion.left()

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ) = if (characterDao.updateCharacterIsFavorite(isFavorite, characterId) != -1) Unit.right()
        else DataBase.Error.Update.left()


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavoriteCharacters(offset: Int): Flow<Result<List<CharacterEntity>>> {
        return try {
            characterDao.getFavoriteCharacters(offset).flatMapLatest { characters ->
                if (characters.isNotEmpty()) flowOf(characters.right())
                else flowOf(emptyList<CharacterEntity>().right())
            }
        } catch (e: SQLiteException) {
            flowOf(DataBase.Error.Reading(e.message).left())
        }
    }
}