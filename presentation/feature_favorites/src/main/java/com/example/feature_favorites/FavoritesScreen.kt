package com.example.feature_favorites

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.R
import com.example.common.component.CharacterCard
import com.example.common.component.CircularLoadingBar
import com.example.common.component.EmptyScreen
import com.example.common.component.RemoveAlertDialog
import com.example.common.screen.ScreenState
import com.example.feature_favorites.paginator.FavoritePaginator
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

    val lazyColumnState = rememberLazyListState()
    val shouldStartPaginate = remember {
        derivedStateOf {
            viewModel.canPaginate && (lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: 0) >= (lazyColumnState.layoutInfo.totalItemsCount - 1)
        }
    }

    RememberLifeCycleEvent { viewModel.onEvent(FavoritesScreenEvent.OnCancelCollectData()) }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value) viewModel.onEvent(FavoritesScreenEvent.OnLoadData())
    }

    FavoritesScreenContent(
        lazyColumState = { lazyColumnState },
        favoritesState = { favoritesState },
        pagingState = { paginationState },
        onItemClick = onItemClick,
        onRemoveCharacter = { isFavorite, characterId ->
            viewModel.onEvent(
                FavoritesScreenEvent.OnRemoveFavorite(
                    isFavorite,
                    characterId
                )
            )
        },
        onLoadMoreCharacters = { viewModel.onEvent(FavoritesScreenEvent.OnLoadData()) },
        emptyListMessage = R.string.empty_favorite_list,
        modifier = Modifier
    )
}

@Composable
fun FavoritesScreenContent(
    lazyColumState: () -> LazyListState,
    favoritesState: () -> ScreenState<List<CharacterVo>>,
    pagingState: () -> FavoritePaginator.State,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onRemoveCharacter: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    emptyListMessage: Int? = null,
    modifier: Modifier,
) {

    val context = LocalContext.current
    when (val state = favoritesState()) {
        is ScreenState.Loading -> {
            CircularLoadingBar(stringResource(id = R.string.character_list_loading))
        }

        is ScreenState.Error -> {
            emptyListMessage?.let {
                EmptyScreen(message = UiText.StringResources(emptyListMessage).asString(context))
            }
        }

        is ScreenState.Empty -> {
            emptyListMessage?.let {
                EmptyScreen(message = UiText.StringResources(emptyListMessage).asString(context))
            }
        }

        is ScreenState.Success -> {
            FavoritesScreenListSuccessContent(
                lazyColumState = lazyColumState,
                items = { state.data },
                pagingState = pagingState,
                onItemClick = onItemClick,
                onRemoveCharacter = onRemoveCharacter,
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
    pagingState: () -> FavoritePaginator.State,
    onItemClick: (itemId: Int, locationId: Int?) -> Unit,
    onRemoveCharacter: (isFavorite: Boolean, characterId: Int) -> Unit,
    onLoadMoreCharacters: () -> Unit,
    modifier: Modifier,
) {
    // TODO: find difference between doing with by and =
    val openAlertDialog = remember { mutableStateOf(false) }
    val characterIdToRemove = remember { mutableStateOf(-1) }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(state = lazyColumState()) {
            with(items()) {
                items(this.size) { index ->
                    val character = this@with[index]
                    CharacterCard(
                        item = { character },
                        onItemClick = onItemClick,
                        onToggleSave = { _, characterId ->
                            openAlertDialog.value = true
                            characterIdToRemove.value = characterId
                        },
                        modifier = modifier
                    )
                }
            }
        }

        HandleScreenState(pagingState = { pagingState() }, boxScope = this)

        OpenAlertDialog(
            openAlertDialog = { openAlertDialog.value },
            closeOpenAlertDialog = { openAlertDialog.value = false },
            characterId = { characterIdToRemove.value },
            confirmAction = onRemoveCharacter
        )
    }
}

@Composable
fun HandleScreenState(
    pagingState: () -> FavoritePaginator.State,
    boxScope: BoxScope,
    modifier: Modifier = Modifier
) = with(boxScope) {
    when (pagingState()) {
        is FavoritePaginator.State.Idle -> {}
        is FavoritePaginator.State.Loading ->
            CircularProgressIndicator(
                modifier = modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.tertiary
            )

        is FavoritePaginator.State.End ->
            // TODO: Custom toast with builder
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.end_of_pagination),
                Toast.LENGTH_SHORT
            ).show()
    }
}

@Composable
fun OpenAlertDialog(
    openAlertDialog: () -> Boolean,
    closeOpenAlertDialog: () -> Unit,
    characterId: () -> Int,
    confirmAction: (isFavorite: Boolean, characterId: Int) -> Unit,
) {
    if (openAlertDialog()) {
        RemoveAlertDialog(
            onDismissRequest = { closeOpenAlertDialog() },
            onConfirmation = {
                closeOpenAlertDialog()
                confirmAction(false, characterId())
            },
            dialogTitle = R.string.dialog_remove_character,
            confirmationText = R.string.remove_character,
            dennyText = R.string.cancel,
            icon = Icons.Outlined.Delete
        )
    }
}

@Composable
fun RememberLifeCycleEvent(
    onStop: () -> Unit
) {
    with(LocalLifecycleOwner.current) {
        val lifeCycleObserver = remember {
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_STOP -> onStop() //this is needed because if we swtich scren again then collect from pagiantor will continue collecting
                    else -> {}
                }
            }
        }

        DisposableEffect(this) {
            this@with.lifecycle.addObserver(lifeCycleObserver)
            onDispose {
                this@with.lifecycle.removeObserver(lifeCycleObserver)
            }
        }
    }
}

