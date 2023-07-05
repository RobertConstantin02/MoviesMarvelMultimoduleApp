package com.example.heroes_presentation.heroFavoritesScreen

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val heroFavoritesScreenRoute = "hero_favorites_screen_route"

fun NavController.navigateToHeroFavoritesScreen(navOptions: NavOptions? = null) {
    this.navigate(heroFavoritesScreenRoute, navOptions)
}