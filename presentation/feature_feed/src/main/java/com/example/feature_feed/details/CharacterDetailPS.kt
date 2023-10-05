package com.example.feature_feed.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailPresentationScreen(
    viewModel: DetailViewModel = hiltViewModel()
) {
    viewModel.getCharacterDetails()
    Text(text = "Details")
}