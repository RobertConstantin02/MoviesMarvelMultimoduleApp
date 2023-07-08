package com.example.favorites_presentation.heroFavoritesScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.navigationlogic.Command
import com.example.navigationlogic.Feature


fun NavController.navigateToHeroFavoritesScreen(
    navOptions: NavOptions? = null,
    heroFavoritesScreenRoute: String
) {
    this.navigate(heroFavoritesScreenRoute, navOptions)
}

fun NavGraphBuilder.favoritesGraph(
    command: Command<Feature>,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onCharacterClick: (heroId: Int) -> Unit,
) {
    navigation(
        startDestination = command.route,
        route = command.feature.route
    ) {
        composable(
            route = command.route,
            arguments = command.args
        ) {
            FavoritesScreen()
        }
        nestedGraphs()
    }

//    command.asEntryPoint { entryPoint ->
//        navigation(
//            startDestination = entryPoint.route,
//            route = favoritesFeature.route
//        ) {
//            composable(
//                route = entryPoint.route,
//                arguments = entryPoint.args
//            ) {
//                FavoritesScreen()
//            }
//            nestedGraphs()
//        }
//    }
}