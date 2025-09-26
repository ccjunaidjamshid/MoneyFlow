// MainActivity.kt
package com.example.moneyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow.presentation.navigation.AppNavGraph
import com.example.moneyflow.presentation.navigation.Screen
import com.example.moneyflow.presentation.ui.components.AddTransactionFab
import com.example.moneyflow.presentation.ui.components.AddTransactionDialog
import com.example.moneyflow.presentation.ui.components.BottomBar
import com.example.moneyflow.presentation.ui.components.DrawerContent
import com.example.moneyflow.presentation.ui.components.MoneyflowTopBar
import com.example.moneyflow.presentation.ui.theme.MoneyflowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Navigation drawer menu items
data class DrawerMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)

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

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Drawer menu items
                val drawerMenuItems = listOf(
                    DrawerMenuItem(Icons.Filled.Search, "Search", "Find transactions and categories"),
                    DrawerMenuItem(Icons.Filled.Star, "History", "View transaction history"),
                    DrawerMenuItem(Icons.Filled.Settings, "Settings", "App preferences and configuration"),
                    DrawerMenuItem(Icons.Filled.Info, "About", "App information and support"),
                    DrawerMenuItem(Icons.Filled.Warning, "Export Records", "Export your records"),
                    DrawerMenuItem(Icons.Filled.ExitToApp, "Sign Out", "Log out from your account")
                )

                val showBottomBar = when (currentRoute) {
                    Screen.Home.route,
                    Screen.Analysis.route,
                    Screen.SetBudget.route,
                    Screen.Category.route,
                    Screen.Account.route -> true
                    else -> false
                }
                val showUI = currentRoute != Screen.Splash.route

                var showAddDialog by remember { mutableStateOf(false) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier,
                            drawerContainerColor = Color.White
                        ) {
                            DrawerContent(
                                menuItems = drawerMenuItems,
                                onItemClick = { item ->
                                    scope.launch { drawerState.close() }
                                    when (item.title) {
                                        "Search" -> navController.navigate(Screen.Search.route)
                                        "History" -> navController.navigate(Screen.History.route)
                                        "Settings" -> navController.navigate(Screen.Settings.route)
                                        "About" -> navController.navigate(Screen.About.route)
                                        "Export Records" -> navController.navigate(Screen.Export.route)
                                        "Sign Out" -> {
                                            // TODO: handle sign out
                                        }
                                    }
                                }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                if (showUI) {
                                    MoneyflowTopBar(
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onSearchClick = {
                                            // Handle search click
                                            navController.navigate(Screen.Search.route)
                                        }
                                    )
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
                                if (showUI && showBottomBar) {
                                    AddTransactionFab(
                                        onClick = {
                                            showAddDialog = true
                                        }
                                    )
                                }
                                if (showAddDialog) {
                                    AddTransactionDialog(
                                        onDismiss = { showAddDialog = false },
                                        onAdd = { amount, type, accountId, categoryId, note ->
                                            // TODO: handle add transaction logic
                                            // Create Transaction object and save to database
                                            println("Adding transaction: Amount=$amount, Type=$type, AccountId=$accountId, CategoryId=$categoryId, Note=$note")
                                            showAddDialog = false
                                        }
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
                )
            }
        }
    }
}