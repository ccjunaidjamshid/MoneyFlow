package com.example.moneyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow.presentation.navigation.AppNavGraph
import com.example.moneyflow.presentation.navigation.Screen
import com.example.moneyflow.presentation.ui.components.AddTransactionFab
import com.example.moneyflow.presentation.ui.components.BottomBar
import com.example.moneyflow.presentation.ui.components.MoneyflowTopBar
import com.example.moneyflow.presentation.ui.theme.MoneyflowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyflowTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp
                val isTablet = screenWidth >= 600

                val showBottomBar = when (currentRoute) {
                    Screen.Home.route,
                    Screen.Analysis.route,
                    Screen.SetBudget.route,
                    Screen.Category.route,
                    Screen.Account.route -> true
                    else -> false
                }
                
                // Control when to show the TopBar and FAB - don't show on splash screen
                val showUI = currentRoute != Screen.Splash.route
                
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    topBar = {
                        if (showUI) {
                            MoneyflowTopBar(title = "Moneyflow")
                        }
                    },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(
                                navController = navController,
                                isTablet = isTablet
                            )
                        }
                    },
                    floatingActionButton = {
                        if (showUI) {
                            AddTransactionFab(
                                onClick = {
                                    navController.navigate(Screen.AddTransaction.route)
                                },
                                isTablet = isTablet
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
