package com.example.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Holds Material icons [ImageVector]s and custom icons asss a drawable Id
 */
object AppIcons {
    /**BottomNavigation**/
    val heroesListBottomIconFilled = Icons.Filled.List
    val heroesListBottomIconBorder = Icons.Outlined.List
    val favoriteBottomIconFilled = Icons.Rounded.Favorite
    val favoriteBottomIconOutlined = Icons.Filled.Favorite
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector): Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int): Icon()
}