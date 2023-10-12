package com.example.udemycourseapp.navigation.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.example.udemycourseapp.navigation.RickMortiTopLevelDestination
import com.example.udemycourseapp.util.RickMortyNavigationBarColorDefaults
import com.example.udemycourseapp.util.isTopLevelDestinationInHierarchy

@Composable
fun MarvelBottomBar(
    destinations: List<RickMortiTopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigate: (destination: RickMortiTopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
){
    RickAndMortyNavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val itemSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            RickAndMortyNavigationBarItem(
                selected = itemSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    RickAndMortyIconSelector(
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
fun RickAndMortyNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = RickMortyNavigationBarColorDefaults.bottomNavigationContainerColor(),
        contentColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
        tonalElevation = 0.dp,
        content = content
    )
}

@Composable
fun RowScope.RickAndMortyNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = RickMortyNavigationBarColorDefaults.bottomNavigationSelectedItemColor(),
            unselectedIconColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
            selectedTextColor = RickMortyNavigationBarColorDefaults.bottomNavigationSelectedItemColor(),
            unselectedTextColor = RickMortyNavigationBarColorDefaults.bottomNavigationContentColor(),
            indicatorColor = RickMortyNavigationBarColorDefaults.bottomNavigationIndicatorColor(),
        )
    )
}
