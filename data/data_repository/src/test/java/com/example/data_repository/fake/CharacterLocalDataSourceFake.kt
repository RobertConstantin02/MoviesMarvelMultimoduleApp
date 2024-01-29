package com.example.data_repository.fake

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.local.DatabaseUnifiedError
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CharacterLocalDataSourceFake : ICharacterLocalDatasource {

    private val characters =
        MutableStateFlow<List<CharacterEntity>?>(emptyList()) //init list with fale data fcrom json?. At the beggining i dont think so because it will call remotemediator which and we will pass out fakes?

    // TODO: create set for characters in order to simulate that the database has already data inside

    private val pagingKeys: MutableList<PagingKeys> = mutableListOf()

    /**
     * replicates an error in the local database
     */
    var localError: DatabaseResponseError<Unit>? =
        DatabaseResponseError(DatabaseUnifiedError.Reading)

    override fun getAllCharacters(): PagingSource<Int, CharacterEntity> =
        object : PagingSource<Int, CharacterEntity>() {
            override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int {
                return 0
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
                return LoadResult.Page(
                    data = this@CharacterLocalDataSourceFake.characters.value.orEmpty(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }

    override suspend fun getCharacterById(
        characterId: Int
    ): Flow<DatabaseResponse<CharacterEntity>> {
        localError?.let { return characters.map { getDatabaseError() } }

        return characters.map { charactersEntity ->
            charactersEntity?.singleOrNull { characterEntity ->
                characterEntity.id == characterId
            }?.let { charactersById -> DatabaseResponseSuccess(charactersById) }
                ?: DatabaseResponseEmpty()
        }
    }

    /**
     * emptyList or null -> DatabaseResponseEmpty
     */
    override suspend fun getCharactersByIds(
        characterIds: List<Int>
    ): Flow<DatabaseResponse<List<CharacterEntity>>> {
        localError?.let { return characters.map { getDatabaseError() } }

        return characters.map { charactersEntity ->
            charactersEntity?.filter { characterEntity ->
                characterEntity.id in characterIds
            }?.let { charactersByIds ->
                if (charactersByIds.isEmpty()) {
                    DatabaseResponseEmpty()
                } else {
                    DatabaseResponseSuccess(charactersByIds)
                }
            } ?: DatabaseResponseEmpty()
        }
    }

    override suspend fun getPagingKeysById(id: Long): PagingKeys? =
        pagingKeys.firstOrNull { it.itemId == id }

    override suspend fun insertPagingKeys(keys: List<PagingKeys>) {
        pagingKeys.addAll(keys)
    }

    override suspend fun insertCharacters(
        characters: List<CharacterEntity>
    ): DatabaseResponse<Unit> {
        localError?.let { return getDatabaseError() }

        val originalCharacterSize = this.characters.value?.size ?: 0
        this.characters.value = this.characters.value?.plus(characters)?.toSet()?.toList()
        return if ((this.characters.value?.size ?: 0) <= originalCharacterSize) {
            DatabaseResponse.create(DatabaseUnifiedError.Insertion)
        } else DatabaseResponseSuccess(Unit)
    }

    override suspend fun insertCharacter(character: CharacterEntity): DatabaseResponse<Unit> {
        localError?.let { return getDatabaseError() }

        val originalCharacterSize = this.characters.value?.size ?: 0
        this.characters.value = this.characters.value?.toMutableList()?.also {
            it.add(character)
        }
        return if ((this.characters.value?.size ?: 0) <= originalCharacterSize) {
            DatabaseResponse.create(DatabaseUnifiedError.Insertion)
        } else DatabaseResponseSuccess(Unit)
    }

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ): Flow<DatabaseResponse<Unit>> = flow {
        localError?.let { emit(getDatabaseError()) }

        this@CharacterLocalDataSourceFake.characters.map { charactersEntity ->
            charactersEntity?.map { character ->
                if (character.id == characterId) {
                    character.copy(isFavorite = isFavorite)
                } else character
            }
        }
        val updatedCharacter = this@CharacterLocalDataSourceFake.characters.value?.firstOrNull {
            it.id == characterId
        }

        if (updatedCharacter?.isFavorite == isFavorite) {
            emit(DatabaseResponseSuccess(Unit))
        } else emit(DatabaseResponseError(DatabaseUnifiedError.Update))
    }

    override fun getFavoriteCharacters(offset: Int): Flow<DatabaseResponse<List<CharacterEntity>>> {
        localError?.let { return characters.map { getDatabaseError() } }

        return this.characters.map { charactersEntity ->
            charactersEntity?.filter { characterEntity ->
                characterEntity.isFavorite
            }?.let { DatabaseResponseSuccess(it) } ?: DatabaseResponseEmpty()
        }
    }

    private fun <T> getDatabaseError(): DatabaseResponseError<T> =
        when (val error = localError?.databaseUnifiedError) {
            DatabaseUnifiedError.Deletion -> DatabaseResponseError(error)
            DatabaseUnifiedError.Insertion -> DatabaseResponseError(error)
            DatabaseUnifiedError.Update -> DatabaseResponseError(error)
            else -> {
                DatabaseResponseError(DatabaseUnifiedError.Reading)
            }
        }
}