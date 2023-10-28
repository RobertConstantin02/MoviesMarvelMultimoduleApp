package com.example.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ScrollUpButton(
    showButton: () -> Boolean = {false},
    scrollToTop: () -> Unit,
) {
    AnimatedVisibility(visible = showButton(), enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd,
        ) {
            Button(
                onClick = { scrollToTop() },
            ) {
                //Text(text = stringResource(id = R.string.scroll_up), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}