package com.example.udemycourseapp.navigation.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import com.example.udemycourseapp.navigation.RickMortiTopLevelDestination
import com.example.udemycourseapp.util.RickMortyNavigationBarColorDefaults
import com.example.udemycourseapp.util.isTopLevelDestinationInHierarchy

@Composable
fun MarvelNavigationRail(
    destinations: List<RickMortiTopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigate: (destination: RickMortiTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
){
    MarvelNavRail(modifier = modifier) {
        destinations.forEach { destination ->
            val itemSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            MarvelNavigationRailItem(
                selected = itemSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    MarvelNavigationIconSelector(
                        itemSelected = itemSelected,
                        destination = destination
                    )
                },
                label = { Text(stringResource(destination.iconLabelId)) },
            )
        }
    }
}

@Composable
fun MarvelNavRail(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = RickMortyNavigationBarColorDefaults.bottomNavigationContainerColor(),
        contentColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
        content = content
    )
}

@Composable
fun MarvelNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = RickMortyNavigationBarColorDefaults.bottomNavigationSelectedItemColor(),
            unselectedIconColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
            selectedTextColor = RickMortyNavigationBarColorDefaults.bottomNavigationSelectedItemColor(),
            unselectedTextColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
            indicatorColor = RickMortyNavigationBarColorDefaults.bottomNavigationIndicatorColor(),
        )
    )
}
