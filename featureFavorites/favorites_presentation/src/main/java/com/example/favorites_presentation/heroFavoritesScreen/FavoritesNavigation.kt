package com.example.favorites_presentation.heroFavoritesScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.navigationlogic.Feature
import com.example.navigationlogic.NavigationCommand
import com.example.navigationlogic.asEntryPoint


fun NavController.navigateToHeroFavoritesScreen(
    navOptions: NavOptions? = null,
    heroFavoritesScreenRoute: String
) {
    this.navigate(heroFavoritesScreenRoute, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    favoritesFeature: Feature,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onCharacterClick: (heroId: Int) -> Unit,
) {
    NavigationCommand.GoToMain(favoritesFeature).asEntryPoint { entryPoint ->
        navigation(
            startDestination = entryPoint.route,
            route = favoritesFeature.route
        ) {
            composable(
                route = entryPoint.route,
                arguments = entryPoint.args
            ) {
                FavoritesScreen()
            }
            nestedGraphs()
        }
    }
}