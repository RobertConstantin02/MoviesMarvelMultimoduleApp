package com.example.heroes_presentation.feed_screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.heroes_presentation.feed_screen.model.CharacterVo

@Composable
fun HeroListScreen(
    viewModel: FeedViewModel = hiltViewModel()
) {

    val lazyPagingItems: LazyPagingItems<CharacterVo> = viewModel.feedState.collectAsLazyPagingItems()
    Log.d("-----> lazyPagingItems", lazyPagingItems.itemCount.toString())

}