package com.manish.mindora.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.manish.mindora.domain.model.ThemeMode
import com.manish.mindora.presentation.components.DisclaimerDialog
import com.manish.mindora.presentation.navigation.Destination
import com.manish.mindora.presentation.navigation.MindoraBottomBar
import com.manish.mindora.presentation.navigation.MindoraNavHost
import com.manish.mindora.presentation.onboarding.NameSetupScreen
import com.manish.mindora.ui.theme.MindoraTheme

@Composable
fun MindoraApp(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val disclaimerOk by mainViewModel.disclaimerAccepted.collectAsStateWithLifecycle()
    val displayName by mainViewModel.displayName.collectAsStateWithLifecycle()
    val nameReady = displayName.isNotBlank()
    val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }

    MindoraTheme(darkTheme = useDarkTheme) {
        if (!disclaimerOk) {
            DisclaimerDialog(onAcknowledge = { mainViewModel.acknowledgeDisclaimer() })
        } else if (!nameReady) {
            NameSetupScreen(onContinue = { mainViewModel.saveDisplayName(it) })
        } else {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute in setOf(
                Destination.Home.route,
                Destination.Journal.route,
                Destination.Insights.route,
                Destination.Chat.route,
            )
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    if (showBottomBar) {
                        MindoraBottomBar(navController = navController)
                    }
                },
            ) { innerPadding ->
                MindoraNavHost(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
