package com.example.udemycourseapp.navigation

import com.example.udemycourseapp.ui.Route.rickMortyFavoritesScreenRoute
import com.example.udemycourseapp.ui.Route.rickMortyFeedScreenRoute
import com.example.designsystem.icon.AppIcons
import com.example.designsystem.icon.Icon
import com.example.udemycourseapp.R

enum class RickMortiTopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val screenTitleId: Int,
    val iconLabelId: Int,
    val route: String,
) {
    RICK_MORTY_FEED(
        Icon.ImageVectorIcon(AppIcons.heroesListBottomIconFilled),
        Icon.ImageVectorIcon(AppIcons.heroesListBottomIconBorder),
        R.string.rick_morty_screen_title,
        R.string.rick_morty_iconLabel,
        rickMortyFeedScreenRoute,
    ),
    FAVORITES(
        Icon.ImageVectorIcon(AppIcons.favoriteBottomIconFilled),
        Icon.ImageVectorIcon(AppIcons.favoriteBottomIconOutlined),
        R.string.rick_morty_favorites_screen_label,
        R.string.rick_morty_favorites_icon_label,
        rickMortyFavoritesScreenRoute,
    )
}