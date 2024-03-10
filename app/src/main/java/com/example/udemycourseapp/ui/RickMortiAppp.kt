package com.example.udemycourseapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.designsystem.component.AppBackGround
import com.example.designsystem.component.AppContent
import com.example.udemycourseapp.navigation.component.RickAndMortyBottomBar
import com.example.udemycourseapp.navigation.RickMortyNavHost
import com.example.udemycourseapp.navigation.component.RickAndMortyNavigationRail

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RickAndMortyApp(
    windowSizeClass: WindowSizeClass,
    appState: RickAndMortyAppState = rememberAppState(
        windowSize = windowSizeClass
    )
) {
    AppBackGround {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (appState.shouldShowBottomBar) {
                    RickAndMortyBottomBar(
                        appState.rickMortiTopLevelDestinations,
                        appState.currentDestination,
                        onNavigate = appState::navigateToTopLevelDestination
                    )
                }
            }
        ) {
            AppContent(paddingValues = it) {
                if (appState.shouldShowNavRail) {
                    RickAndMortyNavigationRail(
                        appState.rickMortiTopLevelDestinations,
                        appState.currentDestination,
                        onNavigate = appState::navigateToTopLevelDestination
                    )
                }
                Column(modifier = Modifier.fillMaxSize()) { RickMortyNavHost(appState = appState) }
            }
        }
    }
}

