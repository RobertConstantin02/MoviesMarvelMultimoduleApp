package com.example.common.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.common.R

@Composable
fun RemoveAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: Int,
    dialogText: String? = null,
    confirmationText: Int,
    dennyText: Int,
    icon: ImageVector,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = stringResource(id = confirmationText))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = dennyText))
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = R.string.remove_alert_dialog_description)
            )
        },
        title = { Text(text = stringResource(id = dialogTitle)) },
        text = { dialogText?.let { Text(text = dialogText) } }
    )
}