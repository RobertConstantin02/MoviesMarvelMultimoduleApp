package com.example.common.screen.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.navigationlogic.Command

fun NavGraphBuilder.detailsScreen(
    command: Command,
    onBackClick: () -> Unit = {}
) {
    composable(
        route = command.getRoute(),
        arguments = command.args
    ) {
        DetailPresentationScreen()
    }
}