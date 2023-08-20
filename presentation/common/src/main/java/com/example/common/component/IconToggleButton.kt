package com.example.common.component

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    uncheckedIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    checkedIcon: @Composable () -> Unit,
) {
    IconToggleButton(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = ToggleButtonDefaults.checkedContainerColor(),
            checkedContentColor = ToggleButtonDefaults.checkedContentColor()
        )
    ) {
        if (checked) checkedIcon() else uncheckedIcon()
    }
}

object ToggleButtonDefaults {
    @Composable
    fun checkedContainerColor() = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun checkedContentColor() = MaterialTheme.colorScheme.onPrimaryContainer

}