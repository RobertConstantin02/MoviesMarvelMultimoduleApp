package com.example.heroes_presentation.heroListScreen

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val heroListScreenRoute = "hero_list_screen_route"

fun NavController.navigateToHeroListScreen(navOptions: NavOptions? = null) {
    this.navigate(heroListScreenRoute, navOptions)
}