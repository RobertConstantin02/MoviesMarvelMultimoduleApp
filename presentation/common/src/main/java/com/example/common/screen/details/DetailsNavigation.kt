package com.example.common.screen.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.navigationlogic.Command

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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