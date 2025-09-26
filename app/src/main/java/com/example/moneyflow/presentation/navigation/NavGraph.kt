package com.example.moneyflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moneyflow.presentation.ui.screens.account.AccountScreen
import com.example.moneyflow.presentation.ui.screens.analysis.AnalysisScreen
import com.example.moneyflow.presentation.ui.screens.category.CategoryScreen
import com.example.moneyflow.presentation.ui.screens.home.HomeScreen
import com.example.moneyflow.presentation.ui.screens.setbudget.SetBudgetScreen
import com.example.moneyflow.presentation.ui.splash.SplashScreen
import com.example.moneyflow.presentation.ui.screens.search.SearchScreen
import com.example.moneyflow.presentation.ui.screens.history.HistoryScreen
import com.example.moneyflow.presentation.ui.screens.settings.SettingsScreen
import com.example.moneyflow.presentation.ui.screens.about.AboutScreen
import com.example.moneyflow.presentation.ui.screens.export.ExportScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen()
            LaunchedEffect(true) {
                delay(2000)
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Analysis.route) { AnalysisScreen() }
        composable(Screen.SetBudget.route) { SetBudgetScreen() }
        composable(Screen.Category.route) { CategoryScreen() }
        composable(Screen.Account.route) { AccountScreen() }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
        composable(Screen.About.route) { AboutScreen() }
        composable(Screen.Export.route) { ExportScreen() }
    }
}
