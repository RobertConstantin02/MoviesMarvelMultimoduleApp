package com.example.udemycourseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.feature_feed.feedGraph
import com.example.feature_feed.mainFeedDetailsScreen
import com.example.navigationlogic.NavigationCommand
import com.example.udemycourseapp.ui.MarvelAppState
import com.example.udemycourseapp.ui.RickMortyAppFeature

@Composable
fun RickMortyNavHost(
    appState: MarvelAppState,
    modifier: Modifier = Modifier,
    startDestination: String = RickMortyAppFeature.RICK_MORTY_FEED.route
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination
    ) {
        feedGraph(
            command = NavigationCommand.GoToMain(RickMortyAppFeature.RICK_MORTY_FEED),
            onItemClick = {
                appState.navController.navigate(
                    NavigationCommand.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED_DETAILS).route
                )
            },
            nestedGraphs = {
                mainFeedDetailsScreen(NavigationCommand.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED_DETAILS))
            }
        )
//        favoritesGraph(
//            command = NavigationCommand.GoToMain(RickMortyAppFeature.FAVORITES),
//            onCharacterClick = {
//
//            },
//            nestedGraphs = {
//
//            }
//        )
    }
}