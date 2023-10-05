package com.example.feature_feed.list

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.R
import com.example.common.component.CircularLoadingBar
import com.example.common.component.ErrorScreen
import com.example.common.component.FeedCardItem
import com.example.common.util.ListExtensions.gridItems
import com.example.presentation_model.CharacterVo

@Composable
fun HeroListScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onItemClick: (itemId: Int) -> Unit
) {

    val lazyPagingItems: LazyPagingItems<CharacterVo> =
        viewModel.feedState.collectAsLazyPagingItems()
    Log.d("-----> lazyPagingItems", lazyPagingItems.itemCount.toString())

    FeedScreenContent(
        items = { lazyPagingItems },
        onItemClick = onItemClick,
        onToggleSave = {

        },
        addToFavorites = {

        },
        onRefresh = { /*TODO*/ },
        modifier = Modifier
    )

}

@Composable
fun FeedScreenContent(
    items: () -> LazyPagingItems<CharacterVo>,
    onItemClick: (itemId: Int) -> Unit,
    onToggleSave: (Boolean) -> Unit,
    addToFavorites: (CharacterVo) -> Unit, ///???
    onRefresh: () -> Unit,
    modifier: Modifier,
) {

    val lazyGridState = rememberLazyGridState()
    val showScrollToTopButton = remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex > 0
        }
    }

    when (items().loadState.mediator?.refresh) {
        is LoadState.Loading -> CircularLoadingBar(stringResource(id = R.string.character_list_loading))
        is LoadState.Error -> ErrorScreen(R.string.list_try_again) { onRefresh() }
        else -> FeedScreenSuccessContent(
            modifier = modifier,
            state = { lazyGridState },
            items = items,
            onItemClick = onItemClick,
            onToggleSave = onToggleSave
        )
    }
}

@Composable
private fun FeedScreenSuccessContent(
    state: () -> LazyGridState,
    items: () -> LazyPagingItems<CharacterVo>,
    onItemClick: (itemId: Int) -> Unit,
    onToggleSave: (Boolean) -> Unit,
    modifier: Modifier,
) {

    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Adaptive(150.dp),
        state = state(),
    ) {
        gridItems(items = items(), key = {
            it.id
        }, itemContent = { character ->
            character?.let {
                FeedCardItem(
                    item = { it },
                    onItemClick = onItemClick,
                    onToggleSave = onToggleSave,
                    modifier = modifier
                )
            }
        })
    }
}