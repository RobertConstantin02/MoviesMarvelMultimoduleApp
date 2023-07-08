package com.example.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A class to model background color and tonal elevation values.
 */
@Immutable
data class BackGroundTheme(
    val color: Color = Color.Unspecified
)

/**
 * A composition local for [BackgroundTheme].
 * uses static so that all composable functions where this is used will trigger
 * recomposition if the background changes.
 */
val LocalBackGroundTheme = staticCompositionLocalOf { BackGroundTheme() }