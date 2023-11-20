package com.example.common.util

import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun CacheDrawScope.drawGradient(colorStops: List<Pair<Float, Color>>) = onDrawWithContent {
    drawContent()
    drawRect(
        Brush.verticalGradient(*colorStops.toTypedArray())
    )
}