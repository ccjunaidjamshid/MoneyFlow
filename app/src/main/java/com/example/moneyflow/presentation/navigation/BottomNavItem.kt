package com.example.moneyflow.presentation.navigation

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
)
