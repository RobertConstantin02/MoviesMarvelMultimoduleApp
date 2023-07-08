package com.example.udemycourseapp.util

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.udemycourseapp.navigation.RickMortiTopLevelDestination

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: RickMortiTopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false