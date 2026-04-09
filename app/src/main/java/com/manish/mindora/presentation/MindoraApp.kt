package com.manish.mindora.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.manish.mindora.presentation.components.DisclaimerDialog
import com.manish.mindora.presentation.navigation.MindoraBottomBar
import com.manish.mindora.presentation.navigation.MindoraNavHost
import com.manish.mindora.ui.theme.MindoraTheme

@Composable
fun MindoraApp(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val disclaimerOk by mainViewModel.disclaimerAccepted.collectAsStateWithLifecycle()

    MindoraTheme {
        if (!disclaimerOk) {
            DisclaimerDialog(onAcknowledge = { mainViewModel.acknowledgeDisclaimer() })
        } else {
            val navController = rememberNavController()
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { MindoraBottomBar(navController = navController) },
            ) { innerPadding ->
                MindoraNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
