package com.example.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.designsystem.theme.LocalSpacing

@Composable
fun ErrorScreen(messageResource: Int, retry: () -> Unit) {
    val dimens = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = messageResource),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        Button(
            onClick = { retry() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.spaceMedium)
        ) {
            Text(
                text = stringResource(id = messageResource),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}