package com.example.udemycourseapp.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

@Composable
fun MarvelAndMoviesApp(
    windowSizeClass: WindowSizeClass,
    appState: AppState = rememberAppState(
        windowSize = windowSizeClass
    )
) {

}