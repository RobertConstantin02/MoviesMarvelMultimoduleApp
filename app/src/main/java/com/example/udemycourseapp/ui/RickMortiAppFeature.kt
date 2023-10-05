package com.example.udemycourseapp.ui

import com.example.navigationlogic.Feature
import com.example.udemycourseapp.ui.Route.rickMortyFavoritesScreenRoute
import com.example.udemycourseapp.ui.Route.rickMortyFeedScreenDetailsRoute
import com.example.udemycourseapp.ui.Route.rickMortyFeedScreenRoute


object Route {
    const val rickMortyFavoritesScreenRoute = "rick_morty_favorites_route"
    const val rickMortyFeedScreenRoute = "rick_morty_feed_route"
    const val rickMortyFeedScreenDetailsRoute = "rick_morty_feed_details_route"
}

enum class RickMortyAppFeature(override val route: String): Feature {
    RICK_MORTY_FEED(rickMortyFeedScreenRoute),
    RICK_MORTY_FEED_DETAILS(rickMortyFeedScreenDetailsRoute),
    FAVORITES(rickMortyFavoritesScreenRoute)
}