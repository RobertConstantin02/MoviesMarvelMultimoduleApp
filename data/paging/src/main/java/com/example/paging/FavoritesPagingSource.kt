package com.example.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.database.detasource.character.ICharacterLocalDatasource
import com.example.database.entities.CharacterEntity
import javax.inject.Inject

//class FavoritesPagingSource @Inject constructor(
//    private val localDataSource: ICharacterLocalDatasource
//    ): PagingSource<Int, CharacterEntity>() {
//
//    override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {
//        val page = params.key ?: 0
//        return try {
//            val favoriteCharacters = localDataSource.getFavoriteCharacters(params.loadSize, page * params.loadSize)
//            LoadResult.Page(
//                data = favoriteCharacters.fold(
//                    ifLeft = { emptyList() },
//                    ifRight = { it }
//                ),
//                prevKey = if (page == 0) null else page - 1,
//                nextKey = if (favoriteCharacters.isLeft()) null else page + 1
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//}