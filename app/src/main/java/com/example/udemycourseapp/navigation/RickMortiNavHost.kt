package com.example.udemycourseapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.favorites_presentation.heroFavoritesScreen.favoritesGraph
import com.example.heroes_presentation.feed_screen.feedGraph
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
            onCharacterClick = {

            },
            nestedGraphs = {

            }
        )
        favoritesGraph(
            command = NavigationCommand.GoToMain(RickMortyAppFeature.FAVORITES),
            onCharacterClick = {

            },
            nestedGraphs = {

            }
        )
    }
}