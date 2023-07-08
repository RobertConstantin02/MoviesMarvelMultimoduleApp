package com.example.navigationlogic

import android.net.Uri
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface Feature { val route: String }

interface Command<T: Feature> {
    val route: String
    val args: List<NamedNavArgument>
    val feature: T
}

sealed class NavigationCommand(
    val jetAppFeature: Feature,
    val subRoute: String = "main",
    private val navArgs: List<NavArg> = emptyList()
): Command<Feature> {
    data class GoToMain(override val feature: Feature) : NavigationCommand(feature)
    data class GoToDetail(override val feature: Feature) :
        NavigationCommand(feature, DETAIL_SUBROUTE, listOf(NavArg.ITEM_ID)) {
        fun createRoute(itemId: String) =
            "${jetAppFeature.route}/$subRoute/${Uri.encode(itemId)}"
    }
//
//    val route = kotlin.run {
//        "${jetAppFeature.route}/$subRoute"
//            .plus(getMandatoryArguments())
//            .plus(getOptionArguments())

    override val route = kotlin.run { "${jetAppFeature.route}/$subRoute${linkMandatoryOptionalArgs()}"}

    //check if with empty args works fine
    private fun linkMandatoryOptionalArgs(): String = with(navArgs) {
        val (matchingArgs, nonMatchingArgs) = partition { !it.optional }
        val mandatoryArgs = matchingArgs.joinToString("/") { "{${it.key}}" }
        val optionalArgs = nonMatchingArgs.joinToString("&") { "${it.key}={${it.key}}" }
            .let { if (it.isNotEmpty()) "?$it" else "" }

        "$mandatoryArgs$optionalArgs"
    }



//    private fun getMandatoryArguments(): String =
//        navArgs.filterNot { !it.optional }
//            .joinToString("/") { "{${it.key}}" }
//
//    private fun getOptionArguments(): String =
//        navArgs.filter { it.optional }
//            .joinToString("&") { "${it.key}={${it.key}}" }
//            .let { if (it.isNotEmpty()) "?$it" else "" }


    override val args = navArgs.map {
        navArgument(it.key) { it.navType }
    }

    companion object {
        const val DETAIL_SUBROUTE = "detail"
    }
}


//fun NavGraphBuilder.customComposable(
//    navCommand: NavigationCommand,
//    content: @Composable (NavBackStackEntry) -> Unit,
//) {
//    composable(
//        route = navCommand.route,
//        arguments = navCommand.args
//    ) {
//        content(it)
//    }
//}

enum class NavArg(
    val key: String,
    val navType: NavType<*>,
    val optional: Boolean,
) {
    ITEM_ID("itemId", NavType.StringType, true)
}