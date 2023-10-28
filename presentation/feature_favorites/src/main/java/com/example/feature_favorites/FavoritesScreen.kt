package com.example.feature_favorites

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.R
import com.example.common.component.CharacterCard
import com.example.common.component.CircularLoadingBar
import com.example.common.component.EmptyScreen
import com.example.common.component.ScrollUpButton
import com.example.feature_favorites.paginator.PaginationState
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText

@Composable
fun FavoritesScreen(
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {

    val favoritesState by
    viewModel.favoritesState.collectAsStateWithLifecycle()

    val paginationState by viewModel.paginationState.collectAsStateWithLifecycle()

    val lazyColumnListState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            Log.d(
                "-----> currentItem",
                (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    ?: 0).toString()
            )
            Log.d(
                "-----> totalListItem",
                (lazyColumnListState.layoutInfo.totalItemsCount - 1).toString()
            )
            viewModel.canPaginate && (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: 0) >= (lazyColumnListState.layoutInfo.totalItemsCount - 1)
        }
    }
    LaunchedEffect(key1 = shouldStartPaginate.value) {
        Log.d("-----> sohuldStarPagin", shouldStartPaginate.value.toString())
        if (shouldStartPaginate.value) //ye sinitPaginAgain //if im scrolling and I reached las item in the list
            viewModel.loadNextCharacters()
    }


    FavoritesScreenContent(
        lazyColumState = { lazyColumnListState },
        favoritesState = { favoritesState },
        paginationState = { paginationState },
        onItemClick = onItemClick,
        onToggleSave = { isFavorite, characterId ->
            viewModel.updateCharacter(
                isFavorite,
                characterId,
            )
        },
        onLoadMoreCharacters = { viewModel.loadNextCharacters() },
        emptyListMessage = R.string.empty_favorite_list,
        modifier = Modifier
    )
}

@Composable
fun FavoritesScreenContent(
    lazyColumState: () -> LazyListState,
    favoritesState: () -> FavoritesScreenState,
    paginationState: () -> PaginationState,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    emptyListMessage: Int? = null,
    modifier: Modifier,
) {

    val context = LocalContext.current
    when (val state = favoritesState()) {
        is FavoritesScreenState.Loading -> {
            CircularLoadingBar(stringResource(id = R.string.character_list_loading))
        }

        is FavoritesScreenState.Error -> {}
        is FavoritesScreenState.Empty -> { //only we will call emptyy from the view model in thawt case. Good idea to maybe apply for feed
            emptyListMessage?.let {
                EmptyScreen(message = UiText.StringResources(emptyListMessage).asString(context))
            }
        }

        is FavoritesScreenState.Success -> {
            FavoritesScreenListSuccessContent(
                lazyColumState = lazyColumState,
                items = { state.favoriteCharacters },
                paginationState = paginationState,
                onItemClick = onItemClick,
                onToggleSave = onToggleSave,
                onLoadMoreCharacters = onLoadMoreCharacters,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun FavoritesScreenListSuccessContent(
    lazyColumState: () -> LazyListState,
    items: () -> List<CharacterVo>,
    paginationState: () -> PaginationState,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    modifier: Modifier,
) {

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(state = lazyColumState()) {
            with(items()) {
                items(this.size) { index ->
                    val character = this@with[index]
                    CharacterCard(
                        item = { character },
                        onItemClick = onItemClick,
                        onToggleSave = { isFavorite, characterId ->
                            onToggleSave(
                                isFavorite,
                                characterId
                            )
                        },
                        modifier = modifier
                    )
                }
//                item {
//                    when (paginationState()) {
//                        is PaginationState.Idle -> {}
//                        is PaginationState.Loading -> {
//                            CircularLoadingBar(stringResource(id = R.string.character_list_loading))
//                        }
//
//                        is PaginationState.PaginationEnd -> {
//
//                        }
//                    }
//                }
            }
        }
        when (paginationState()) {
            is PaginationState.Idle -> {}
            is PaginationState.Loading -> {
                CircularProgressIndicator(modifier = modifier.align(Alignment.BottomCenter), color = MaterialTheme.colorScheme.tertiary)
            }

            is PaginationState.PaginationEnd -> {
                ScrollUpButton {
                   // lazyColumState().animateScrollToItem(0)
                }
            }
        }

    }

}