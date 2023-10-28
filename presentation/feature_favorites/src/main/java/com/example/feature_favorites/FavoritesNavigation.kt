package com.example.feature_favorites

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.navigationlogic.Command

fun NavGraphBuilder.favoritesGraph(
    command: Command,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    onItemClick: (characterId: Int, locationId: Int?) -> Unit
) {
    navigation(
        startDestination = command.getRoute(),
        route = command.feature.route
    ) {
        composable(
            route = command.getRoute(),
            arguments = command.args
        ) {
            FavoritesScreen(onItemClick = onItemClick)
        }
        nestedGraphs()
    }
}

