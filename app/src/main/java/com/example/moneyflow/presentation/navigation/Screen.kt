package com.example.moneyflow.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Analysis : Screen("analysis")
    object SetBudget : Screen("set_budget")
    object Category : Screen("category")
    object Account : Screen("account")
    object AddTransaction : Screen("add_transaction")
}
