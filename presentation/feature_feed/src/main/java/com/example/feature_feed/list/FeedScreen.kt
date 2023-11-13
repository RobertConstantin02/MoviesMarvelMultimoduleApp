package com.example.feature_feed.list

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.R
import com.example.common.component.CharacterCard
import com.example.common.component.CircularLoadingBar
import com.example.common.component.EmptyScreen
import com.example.common.component.ErrorScreen
import com.example.common.util.ListExtensions.gridItems
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText

@Composable
fun HeroListScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onItemClick: (itemId: Int, locationId: Int?) -> Unit
) {

    val lazyPagingItems: LazyPagingItems<CharacterVo> =
        viewModel.feedState.collectAsLazyPagingItems()

    CharacterScreenListContent(
        items = { lazyPagingItems },
        onItemClick = onItemClick,
        onToggleSave = { isFavorite, characterId -> viewModel.updateCharacter(isFavorite, characterId) },
        onRefresh = { /*TODO*/ },
        modifier = Modifier
    )
}

@Composable
fun CharacterScreenListContent(
    items: () -> LazyPagingItems<CharacterVo>,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    onRefresh: () -> Unit = {},
    emptyListMessage: Int? = null,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lazyGridState = rememberLazyGridState()
    val showScrollToTopButton = remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex > 0
        }
    }

    when ( val pagingState = items().loadState.mediator?.refresh) {
        is LoadState.Loading -> CircularLoadingBar(stringResource(id = R.string.character_list_loading))
        is LoadState.Error -> ErrorScreen(
            //UiText.StringResources(R.string.list_try_again).asString(context)
            UiText.DynamicText(R.string.list_try_again, pagingState.error.message).asString(context)
        ) { onRefresh() }
        else -> {
            if (items().itemCount == 0) {
                emptyListMessage?.let {
                    EmptyScreen(message = UiText.StringResources(emptyListMessage).asString(context))
                }
            } else {
                CharacterScreenListSuccessContent(
                    modifier = modifier,
                    state = { lazyGridState },
                    items = items,
                    onItemClick = onItemClick,
                    onToggleSave = onToggleSave
                )
            }
        }
    }
}
//
@Composable
private fun CharacterScreenListSuccessContent(
    state: () -> LazyGridState,
    items: () -> LazyPagingItems<CharacterVo>,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
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
                CharacterCard(
                    item = { it },
                    onItemClick = onItemClick,
                    onToggleSave = onToggleSave,
                    modifier = modifier
                )
            }
        })
    }
}