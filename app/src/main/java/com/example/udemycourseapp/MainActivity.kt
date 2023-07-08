package com.example.udemycourseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.designsystem.theme.UdemyCourseAppTheme
import com.example.udemycourseapp.ui.MarvelAndMoviesApp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UdemyCourseAppTheme {
                MarvelAndMoviesApp(windowSizeClass = calculateWindowSizeClass(activity = this))
            }
        }
    }
}