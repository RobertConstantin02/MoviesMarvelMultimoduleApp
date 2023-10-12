package com.example.udemycourseapp.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.feature_favorites.favoritesGraph
import com.example.feature_feed.feedGraph
import com.example.feature_feed.mainFeedDetailsScreen
import com.example.navigationlogic.NavigationCommand
import com.example.udemycourseapp.ui.MarvelAppState
import com.example.udemycourseapp.ui.RickMortyAppFeature

private const val FEED_DETAILS = "feedDetail"

class GoToFeedDetails(
    private val characterId: Int,
    private val locationId: Int?,
    ) :
    NavigationCommand.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED) {
    override fun createRoute(): String {
        return super.createRoute().plus(
            "${Uri.encode(characterId.toString())}/${Uri.encode(locationId.toString())}"
        )
    }
}

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
            onItemClick = { itemId, locationId ->
                appState.navController.navigate(
                    GoToFeedDetails(
                        itemId, locationId
                    ).createRoute()
                )
            },
            nestedGraphs = {
                mainFeedDetailsScreen(NavigationCommand.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED))
            }
        )
        favoritesGraph(
            command = NavigationCommand.GoToMain(RickMortyAppFeature.FAVORITES),
            onItemClick = { characterId, locationId ->

            },
            nestedGraphs = {

            }
        )
    }
}