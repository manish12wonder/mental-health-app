package com.manish.mindora.presentation.navigation

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Journal : Destination("journal")
    data object Insights : Destination("insights")
    data object Chat : Destination("chat")
    data object Settings : Destination("settings")
}
