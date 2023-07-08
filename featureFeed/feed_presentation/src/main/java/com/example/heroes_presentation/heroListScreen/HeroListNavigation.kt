package com.example.heroes_presentation.heroListScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.navigationlogic.Feature
import com.example.navigationlogic.NavigationCommand
import com.example.navigationlogic.asEntryPoint


fun NavController.navigateToHeroListScreen(
    navOptions: NavOptions? = null,
    heroListScreenRoute: String
) {
    this.navigate(heroListScreenRoute, navOptions)
}

fun NavGraphBuilder.feedGraph(
    rickMortyFeedFeature: Feature,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onCharacterClick: (heroId: String) -> Unit
) {
    NavigationCommand.GoToMain(rickMortyFeedFeature).asEntryPoint { entryPoint ->
        navigation(
            startDestination = entryPoint.route,
            route = rickMortyFeedFeature.route
        ) {
            composable(
                route = entryPoint.route,
                arguments = entryPoint.args
            ) {
                HeroListScreen()
            }
            nestedGraphs()
        }
    }
}

