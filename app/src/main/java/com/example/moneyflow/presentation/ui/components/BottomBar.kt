package com.example.moneyflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

    // 🎨 Beautiful Clean White Background
    val selectedColor = Color(0xFF66BB6A)     // Darker light green for selected
    val unselectedColor = Color(0xFF9E9E9E)   // Soft gray for unselected
    val selectedIndicatorColor = Color(0xFFF1F8E9) // Very light green indicator

    NavigationBar(
        modifier = Modifier
            .height(if (isTablet) 85.dp else 75.dp)
            .fillMaxWidth(),
        containerColor = Color.White, // Pure white background
        tonalElevation = 12.dp, // Increased elevation for better shadow
        windowInsets = WindowInsets(0)
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
                            .padding(top = if (isTablet) 8.dp else 6.dp)
                            .size(if (isTablet) 26.dp else 22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = if (isTablet) 13.sp else 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) selectedColor else unselectedColor,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = selectedIndicatorColor, // Light green indicator
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}
