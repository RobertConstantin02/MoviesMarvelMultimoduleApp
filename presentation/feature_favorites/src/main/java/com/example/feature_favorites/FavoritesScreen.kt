package com.example.feature_favorites

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FavoritesScreen(
    onItemClick: (itemId: Int, locationId: Int?) -> Unit
) {
    Text(text = "FAVORITES")
}