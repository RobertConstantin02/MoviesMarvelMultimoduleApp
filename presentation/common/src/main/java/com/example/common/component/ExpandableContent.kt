package com.example.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

const val EXPANSION_TRANSITION_DURATION: Int = 500

@Composable
fun ExpandableContent(
    visible: Boolean,
    onExpanded: @Composable () -> Unit,
) {
    val enterTransition = expandVertically(
        expandFrom = Alignment.Top,
        animationSpec = tween(EXPANSION_TRANSITION_DURATION)
    ) + fadeIn(
        initialAlpha = 0.3f,
        animationSpec = tween(EXPANSION_TRANSITION_DURATION)
    )
    val exitTransition =
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        ) + fadeOut(
            animationSpec = tween(EXPANSION_TRANSITION_DURATION)
        )

    AnimatedVisibility(
        visible = visible,
        enter = enterTransition,
        exit = exitTransition,
    ) {
        onExpanded()
    }
}