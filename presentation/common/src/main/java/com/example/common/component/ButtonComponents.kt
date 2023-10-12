package com.example.common.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.common.R
import com.example.designsystem.icon.AppIcons

@Composable
fun ExpandButton(expanded: () -> Boolean, action: (Boolean) -> Unit) {
    IconButton(
        onClick = {
            action(!expanded())
        }) {
        Icon(
            imageVector = if (expanded()) AppIcons.arrowUpFilled else AppIcons.arrowDownFilled,
            contentDescription = stringResource(id = R.string.button_expand_content_description)
        )
    }
}