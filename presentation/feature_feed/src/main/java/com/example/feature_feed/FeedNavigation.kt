package com.example.feature_feed

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.feature_feed.details.DetailPresentationScreen
import com.example.feature_feed.list.HeroListScreen
import com.example.navigationlogic.Command
import com.example.navigationlogic.Feature

fun NavGraphBuilder.mainFeedDetailsScreen(
    command: Command<Feature>,
    onBackClick: () -> Unit = {}
) {
    composable(route = command.route) {
        DetailPresentationScreen() //if I have a tollBar or something we can apply onBack
    }
}

fun NavController.navigateToHeroListScreen(
    navOptions: NavOptions? = null,
    heroListScreenRoute: String
) {
    this.navigate(heroListScreenRoute, navOptions)
}

fun NavGraphBuilder.feedGraph(
    command: Command<Feature>,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onItemClick: (heroId: Int) -> Unit
) {
    navigation(
        startDestination = command.route,
        route = command.feature.route
    ) {
        composable(
            route = command.route,
            arguments = command.args
        ) {
            HeroListScreen(onItemClick = onItemClick)
        }
        nestedGraphs()
    }
}

