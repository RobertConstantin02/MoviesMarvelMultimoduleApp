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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.common.R
import com.example.common.component.CircularLoadingBar
import com.example.common.component.ErrorScreen
import com.example.common.component.CharacterCard
import com.example.common.content.CharacterScreenListContent
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
    Log.d("-----> lazyPagingItems", lazyPagingItems.itemCount.toString())

    CharacterScreenListContent(
        items = { lazyPagingItems },
        onItemClick = onItemClick,
        onToggleSave = { isFavorite, characterId -> viewModel.updateCharacter(isFavorite, characterId) },
        onRefresh = { /*TODO*/ },
        modifier = Modifier
    )

}