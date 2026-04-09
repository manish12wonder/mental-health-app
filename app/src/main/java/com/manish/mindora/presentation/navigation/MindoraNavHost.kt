package com.manish.mindora.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.manish.mindora.presentation.ai.AiFeedbackScreen
import com.manish.mindora.presentation.home.HomeScreen
import com.manish.mindora.presentation.insights.InsightsScreen
import com.manish.mindora.presentation.journal.JournalScreen

@Composable
fun MindoraNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier,
    ) {
        composable(Destination.Home.route) {
            HomeScreen(
                onOpenJournal = {
                    navController.navigate(Destination.Journal.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(Destination.Journal.route) {
            JournalScreen()
        }
        composable(Destination.Insights.route) {
            InsightsScreen()
        }
        composable(Destination.Chat.route) {
            AiFeedbackScreen()
        }
    }
}
