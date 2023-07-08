package com.example.udemycourseapp.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object RickMortyNavigationBarColorDefaults {

    @Composable
    fun bottomNavigationContainerColor() = MaterialTheme.colorScheme.surface

    @Composable
    fun bottomNavigationContentColor() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun bottomNavigationSelectedItemColor() = MaterialTheme.colorScheme.primary

    @Composable
    fun bottomNavigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
