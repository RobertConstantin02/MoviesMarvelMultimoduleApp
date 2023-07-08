package com.example.designsystem.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.designsystem.theme.LocalBackGroundTheme

@Composable
fun AppBackGround(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val color = LocalBackGroundTheme.current.color

    Surface(
        modifier = modifier.fillMaxSize(),
        color = if (color == Color.Unspecified) Color.Transparent else color,
        content = content
    )
}