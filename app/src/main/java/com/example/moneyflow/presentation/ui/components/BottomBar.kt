package com.example.moneyflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.moneyflow.R
import com.example.moneyflow.presentation.navigation.BottomNavItem
import com.example.moneyflow.presentation.navigation.Screen

@Composable
fun BottomBar(
    navController: NavHostController,
    isTablet: Boolean = false
) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Home", R.drawable.home),
        BottomNavItem(Screen.Analysis.route, "Analysis", R.drawable.analysis),
        BottomNavItem(Screen.SetBudget.route, "Budget", R.drawable.budget),
        BottomNavItem(Screen.Category.route, "Category", R.drawable.category),
        BottomNavItem(Screen.Account.route, "Account", R.drawable.account)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 🎨 Splash-inspired colors
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF9FFF9), // White with green tint
            Color(0xFFE8F5E9)  // Very light green
        )
    )

    val selectedColor = Color(0xFF2E7D32)   // Deep green
    val unselectedColor = Color(0xFF757575) // Muted gray

    NavigationBar(
        modifier = Modifier
            .height(if (isTablet) 80.dp else 70.dp)
            .background(brush = gradientBackground),
        containerColor = Color.Transparent,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (selected) selectedColor else unselectedColor,
                        modifier = Modifier
                            .padding(top = if (isTablet) 6.dp else 4.dp)
                            .height(if (isTablet) 28.dp else 24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) selectedColor else unselectedColor
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}
