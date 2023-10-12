package com.example.designsystem.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Info
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
    /**Button Icons**/
    val saveToggleButtonFilled = Icons.Rounded.Favorite
    val saveToggleButtonBorder = Icons.Rounded.Favorite
    val extraInfoIcon = Icons.Rounded.Info
    val arrowUpFilled = Icons.Default.KeyboardArrowUp
    val arrowDownFilled = Icons.Default.KeyboardArrowDown
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector): Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int): Icon()

//    class ImageUrl(url: String?) {
//
//        val value = if (url.isNullOrEmpty()) null else PATH_IMAGE.plus(url)
//    }
}