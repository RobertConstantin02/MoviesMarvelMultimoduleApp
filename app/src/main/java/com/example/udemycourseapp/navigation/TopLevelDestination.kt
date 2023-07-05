package com.example.udemycourseapp.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.udemycourseapp.R
import com.example.udemycourseapp.designSystem.AppIcons

interface TopDestination

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconLabelId: Int,
    val titleScreenId: Int,
): TopDestination {
    HEROES(
        selectedIcon = AppIcons.heroesListBottomIconFilled,
        unselectedIcon = AppIcons.heroesListBottomIconBorder,
        iconLabelId = R.string.heroes_iconLabel,
        titleScreenId = R.string.heroes_screen_title,
    ),
    FAVORITES(
        selectedIcon = AppIcons.favoriteBottomIconFilled,
        unselectedIcon = AppIcons.favoriteBottomIconOutlined,
        iconLabelId = R.string.favorites_heroes_icon_label,
        titleScreenId = R.string.favorites_heroes_screen_title,
    ),
}