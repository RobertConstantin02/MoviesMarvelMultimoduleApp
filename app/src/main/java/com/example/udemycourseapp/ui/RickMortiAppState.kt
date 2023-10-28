package com.example.udemycourseapp.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.udemycourseapp.navigation.RickMortiTopLevelDestination
import com.example.udemycourseapp.ui.Route.rickMortyFavoritesScreenRoute
import com.example.udemycourseapp.ui.Route.rickMortyFeedScreenRoute
import com.example.udemycourseapp.util.navigateToScreen


@Composable
fun rememberAppState(
    windowSize: WindowSizeClass? = null,
    navController: NavHostController = rememberNavController(),
): RickAndMortyAppState = remember(navController, windowSize) {
    RickAndMortyAppState(navController, windowSize)
}

@Stable
class RickAndMortyAppState(
    val navController: NavHostController,
    private val windowSize: WindowSizeClass?,
) {
    //only show bottomBar when is compact screen
    val shouldShowBottomBar: Boolean
        get() = windowSize?.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar
    //gives you the visible composable that the user is currently seeing.
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentRickMortiTopLevelDestination: RickMortiTopLevelDestination?
        @Composable get() = when (currentDestination?.route?.removeSubRoute()) {
            rickMortyFeedScreenRoute -> RickMortiTopLevelDestination.RICK_MORTY_FEED
            rickMortyFavoritesScreenRoute -> RickMortiTopLevelDestination.FAVORITES
            else -> null
        }

    val rickMortiTopLevelDestinations: List<RickMortiTopLevelDestination> = RickMortiTopLevelDestination.values().asList()


    fun navigateToTopLevelDestination(nextRickAndMortyTopLevelDestination: RickMortiTopLevelDestination) { //here can go an interface and when ou use it you will have to pass your implementation.
        navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            if (nextRickAndMortyTopLevelDestination.route == rickMortyFeedScreenRoute)
                restoreState = true
        }.also { navOptions ->
            when (nextRickAndMortyTopLevelDestination) {
                RickMortiTopLevelDestination.RICK_MORTY_FEED ->
                    navController.navigateToScreen(rickMortyFeedScreenRoute, navOptions)
                RickMortiTopLevelDestination.FAVORITES ->
                    navController.navigateToScreen(rickMortyFavoritesScreenRoute, navOptions)
            }
        }
    }
}

fun String.removeSubRoute() = substringBeforeLast("/")
