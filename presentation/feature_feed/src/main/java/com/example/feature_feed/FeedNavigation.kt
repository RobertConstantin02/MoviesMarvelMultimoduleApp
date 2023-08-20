package com.example.feature_feed

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.navigationlogic.Command
import com.example.navigationlogic.Feature


fun NavController.navigateToHeroListScreen(
    navOptions: NavOptions? = null,
    heroListScreenRoute: String
) {
    this.navigate(heroListScreenRoute, navOptions)
}

fun NavGraphBuilder.feedGraph(
    command: Command<Feature>,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onCharacterClick: (heroId: String) -> Unit
) {
    navigation(
        startDestination = command.route,
        route = command.feature.route
    ) {
        composable(
            route = command.route,
            arguments = command.args
        ) {
            HeroListScreen()
        }
        nestedGraphs()
    }
}

