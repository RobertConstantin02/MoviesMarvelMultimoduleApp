package com.example.udemycourseapp.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.example.common.screen.details.detailsScreen
import com.example.feature_favorites.favoritesGraph
import com.example.feature_feed.feedGraph
import com.example.navigationlogic.Navigation
import com.example.udemycourseapp.ui.RickAndMortyAppState
import com.example.udemycourseapp.ui.RickMortyAppFeature

private const val FEED_DETAILS = "feedDetail"

class FeedDetails(
    private val characterId: Int,
    private val locationId: Int?,
    ) : Navigation.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED) {
    override fun createRoute(): String {
        return super.createRoute().plus(
            "${Uri.encode(characterId.toString())}/${Uri.encode(locationId.toString())}"
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RickMortyNavHost(
    appState: RickAndMortyAppState,
    startDestination: String = RickMortyAppFeature.RICK_MORTY_FEED.route
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination
    ) {
        feedGraph(
            command = Navigation.GoToMain(RickMortyAppFeature.RICK_MORTY_FEED),
            onItemClick = { itemId, locationId ->
                appState.navController.navigate(
                    FeedDetails(
                        itemId, locationId
                    ).createRoute()
                )
            },
            nestedGraphs = {
                detailsScreen(Navigation.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED))
            }
        )

        favoritesGraph(
            command = Navigation.GoToMain(RickMortyAppFeature.FAVORITES),
            onItemClick = { itemId, locationId ->
                appState.navController.navigate(FeedDetails(itemId, locationId).createRoute())
            },
            nestedGraphs = {
                detailsScreen(Navigation.GoToDetail(RickMortyAppFeature.RICK_MORTY_FEED))
            }
        )
    }
}