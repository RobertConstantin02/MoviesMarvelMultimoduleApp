package com.example.feature_favorites

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.R
import com.example.common.component.CharacterCard
import com.example.common.component.CircularLoadingBar
import com.example.common.component.EmptyScreen
import com.example.presentation_model.CharacterVo
import com.example.resources.UiText

@Composable
fun FavoritesScreen(
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {

    val favoritesState by
        viewModel.favoritesState.collectAsStateWithLifecycle()

    viewModel.loadNextCharacters()

    FavoritesScreenContent(
        favoritesState = { favoritesState },
        onItemClick = onItemClick,
        onToggleSave = { isFavorite, characterId -> viewModel.updateCharacter(isFavorite, characterId) },
        onLoadMoreCharacters = { viewModel.loadNextCharacters()  } ,
        emptyListMessage = R.string.empty_favorite_list,
        modifier = Modifier
    )
}

@Composable
fun FavoritesScreenContent(
    favoritesState: () -> FavoritesScreenState,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    emptyListMessage: Int? = null,
    modifier: Modifier,
) {
    val lazyGridState = rememberLazyListState()
    val context = LocalContext.current
    when(val state = favoritesState()) {
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
                state = { lazyGridState },
                items = {state.favoriteCharacters},
                endReached = state.endReached,
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
    state: () -> LazyListState,
    items: () -> List<CharacterVo>,
    endReached: Boolean = false,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onToggleSave: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    modifier: Modifier,
) {
    LazyColumn(state = state()) {
        with(items()) {
            items(this.size) { index ->
                val character = this@with[index]
                if ((index > this@with.size - 1) && !endReached) {
                    onLoadMoreCharacters()
                }
                CharacterCard(
                    item = { character },
                    onItemClick = onItemClick,
                    onToggleSave = onToggleSave,
                    modifier = modifier
                )
            }
        }
    }
}