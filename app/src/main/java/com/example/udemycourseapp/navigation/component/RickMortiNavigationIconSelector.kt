package com.example.udemycourseapp.navigation.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.designsystem.icon.Icon
import com.example.udemycourseapp.navigation.RickMortiTopLevelDestination

//is something that really depoends on our app itself. Like our topLevelDestinations which are specific to our app.
@Composable
fun MarvelNavigationIconSelector(itemSelected: Boolean, destination: RickMortiTopLevelDestination) {
    when (
        val icon = if (itemSelected) destination.selectedIcon
        else destination.unselectedIcon
    ) {
        is Icon.ImageVectorIcon -> Icon(
            imageVector = icon.imageVector,
            contentDescription = null, //change for asString() the resource
        )

        is Icon.DrawableResourceIcon -> Icon(
            painter = painterResource(id = icon.id),
            contentDescription = null
        )
    }
}