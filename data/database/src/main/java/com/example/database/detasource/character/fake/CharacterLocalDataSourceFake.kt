package com.example.database.detasource.character.fake

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.core.local.DatabaseResponse
import com.example.core.local.DatabaseResponseEmpty
import com.example.core.local.DatabaseResponseError
import com.example.core.local.DatabaseResponseSuccess
import com.example.core.local.LocalUnifiedError
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import com.example.database.entities.PagingKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.math.min

const val PAGE_SIZE = 20
class CharacterLocalDataSourceFake : ICharacterLocalDatasource {

    private val characters = MutableStateFlow<List<CharacterEntity>?>(emptyList())

    private var pagingKeys: MutableList<PagingKeys> = mutableListOf()

    //errors for remote and local
    var readError: DatabaseResponseError<Unit>? = null
    var insertError: DatabaseResponseError<Unit>? = null
    var updateError: DatabaseResponseError<Unit>? = null
    var databaseEmpty: DatabaseResponseEmpty<Unit>? = null
    //error for pagination
    var paginationError: Boolean = false

    private val pagingSource = object : PagingSource<Int, CharacterEntity>() {
        override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int {
            return state.anchorPosition ?: 1
        }

        //load will give me 10, from what i have saved in db locally. It doesn't matter that from
        // remote it comes 20. I want from my local to take 10 out of those 20.
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
            if (paginationError) {
                return LoadResult.Error(Exception("pagination test error", Throwable("test")))
            }
            val page = params.key ?: 1
            val startIndex = (page - 1) * PAGE_SIZE
            val endIndex = min(startIndex + PAGE_SIZE, this@CharacterLocalDataSourceFake.characters.value?.size ?: 0)
            val dataForThisPage = this@CharacterLocalDataSourceFake.characters.value?.subList(startIndex, endIndex).orEmpty()
            return LoadResult.Page(
                data = dataForThisPage.take(params.loadSize),
                prevKey = if (page > 1) page -1 else null,
                nextKey = if (endIndex < (this@CharacterLocalDataSourceFake.characters.value?.size ?: 0)) page + 1 else null
            )
        }
    }

    fun setCharacters(characters: List<CharacterEntity>?) {
        this.characters.value = characters
    }

    fun setPagingKeys(keys: List<PagingKeys>) {
        pagingKeys = keys.toMutableList()
    }

    override fun getAllCharacters(): PagingSource<Int, CharacterEntity> = pagingSource

    override suspend fun getCharacterById(
        characterId: Int
    ): Flow<DatabaseResponse<CharacterEntity>> {
        readError?.let { return characters.map { getDatabaseError() } }
        if (databaseEmpty != null) return characters.map { DatabaseResponseEmpty() }

        return characters.map { charactersEntity ->
            charactersEntity?.singleOrNull { characterEntity ->
                characterEntity.id == characterId
            }?.let { charactersById -> DatabaseResponseSuccess(charactersById) }
                ?: DatabaseResponseEmpty()
        }
    }

    override suspend fun getCharactersByIds(
        characterIds: List<Int>
    ): Flow<DatabaseResponse<List<CharacterEntity>>> {
        readError?.let { return characters.map { getDatabaseError() } }
        if (databaseEmpty != null) return characters.map { DatabaseResponseEmpty() }

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
        readError?.let { return getDatabaseError() }
        insertError?.let { return DatabaseResponseError(LocalUnifiedError.Insertion) }

        val originalCharacterSize = this.characters.value?.size ?: 0
        this.characters.value = this.characters.value?.plus(characters)?.toSet()?.toList()
        return if ((this.characters.value?.size ?: 0) <= originalCharacterSize) {
            DatabaseResponse.create(LocalUnifiedError.Insertion)
        } else DatabaseResponseSuccess(Unit)
    }

    override suspend fun insertCharacter(character: CharacterEntity): DatabaseResponse<Unit> {
        insertError?.let { return DatabaseResponseError(LocalUnifiedError.Insertion) }

        val originalCharacterSize = this.characters.value?.size ?: 0
        this.characters.value = this.characters.value?.toMutableList()?.also {
            it.add(character)
        }
        return if ((this.characters.value?.size ?: 0) <= originalCharacterSize) {
            DatabaseResponse.create(LocalUnifiedError.Insertion)
        } else DatabaseResponseSuccess(Unit)
    }

    override suspend fun updateCharacterIsFavorite(
        isFavorite: Boolean,
        characterId: Int
    ): Flow<DatabaseResponse<Unit>> = flow {
        updateError?.let { emit(DatabaseResponseError(LocalUnifiedError.Update)) }
        characters.value = characters.value?.map { character ->
            if (character.id == characterId) {
                println("${character.copy(isFavorite = isFavorite)}")
                character.copy(isFavorite = isFavorite)
            } else character
        }
        val updatedCharacter = characters.value?.firstOrNull {
            it.id == characterId
        }
        if (updatedCharacter?.isFavorite == isFavorite) {
            emit(DatabaseResponseSuccess(Unit))
        } else emit(DatabaseResponseError(LocalUnifiedError.Update))
    }

    override fun getFavoriteCharacters(offset: Int): Flow<DatabaseResponse<List<CharacterEntity>>> {
        readError?.let { return characters.map { getDatabaseError() } }
        return characters.map { charactersEntity ->
            charactersEntity?.subList(offset, offset + 10)?.filter { characterEntity ->
                characterEntity.isFavorite
            }?.let { characters ->
                if (characters.isEmpty())DatabaseResponseEmpty()
                else DatabaseResponseSuccess(characters)
            } ?: DatabaseResponseEmpty()
        }
    }

    private fun <T> getDatabaseError(): DatabaseResponseError<T> =
        when (val error = readError?.localUnifiedError) {
            LocalUnifiedError.Deletion -> DatabaseResponseError(error)
            LocalUnifiedError.Insertion -> DatabaseResponseError(error)
            LocalUnifiedError.Update -> DatabaseResponseError(error)
            else -> DatabaseResponseError(LocalUnifiedError.Reading)
        }
}