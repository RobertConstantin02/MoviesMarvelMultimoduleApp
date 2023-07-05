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
import com.example.heroes_presentation.heroFavoritesScreen.heroFavoritesScreenRoute
import com.example.heroes_presentation.heroFavoritesScreen.navigateToHeroFavoritesScreen
import com.example.heroes_presentation.heroListScreen.heroListScreenRoute
import com.example.heroes_presentation.heroListScreen.navigateToHeroListScreen
import com.example.udemycourseapp.navigation.TopLevelDestination

interface AppState

@Composable
fun rememberAppState(
    windowSize: WindowSizeClass? = null,
    navController: NavHostController = rememberNavController(),
): AppState = remember(navController, windowSize) {
    MarvelMoviesAppState(navController, windowSize)
}

@Stable
class MarvelMoviesAppState(
    val navController: NavHostController,
    private val windowSize: WindowSizeClass?,
): AppState {
    //only show bottomBar when is compact screen
    val shouldShowBottomBar: Boolean
        get() = windowSize?.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar
    //gives you the visible composable that the user is currently seeing.
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            heroListScreenRoute -> TopLevelDestination.HEROES
            heroFavoritesScreenRoute -> TopLevelDestination.FAVORITES
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()


    fun navigateToTopLevelDestination(nextTopLevelDestination: TopLevelDestination) { //here can go an interface and when ou use it you will have to pass your implementation.
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
            restoreState = true
        }.also { navOptions ->
            when (nextTopLevelDestination) {
                TopLevelDestination.HEROES ->
                    navController.navigateToHeroListScreen(navOptions)
                TopLevelDestination.FAVORITES ->
                    navController.navigateToHeroFavoritesScreen(navOptions)
            }
        }
    }
}
