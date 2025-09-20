package com.example.moneyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow.presentation.navigation.AppNavGraph
import com.example.moneyflow.presentation.navigation.Screen
import com.example.moneyflow.presentation.ui.components.AddTransactionFab
import com.example.moneyflow.presentation.ui.components.BottomBar
import com.example.moneyflow.presentation.ui.theme.AppColors
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
                    DrawerMenuItem(Icons.Default.Search, "Search", "Find transactions and categories"),
                    DrawerMenuItem(Icons.Default.Star, "History", "View transaction history"),
                    DrawerMenuItem(Icons.Default.Settings, "Settings", "App preferences and configuration"),
                    DrawerMenuItem(Icons.Default.Info, "About", "App information and support"),
                    DrawerMenuItem(Icons.Default.ExitToApp, "Sign Out", "Log out from your account")
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
                
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(280.dp),
                            drawerContainerColor = Color.White
                        ) {
                            DrawerContent(
                                menuItems = drawerMenuItems,
                                onItemClick = { item ->
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    // Handle navigation based on item
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
                                        onMenuClick = {
                                            scope.launch {
                                                drawerState.open()
                                            }
                                        },
                                        onSearchClick = {
                                            // Handle search click
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
                )
            }
        }
    }
}

@Composable
fun MoneyflowTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Icon
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF66BB6A), // Darker light green
                    modifier = Modifier.size(24.dp)
                )
            }

            // App Name
            Text(
                text = "MoneyFlow",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF66BB6A), // Darker light green
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Search Icon
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF66BB6A), // Darker light green
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    menuItems: List<DrawerMenuItem>,
    onItemClick: (DrawerMenuItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Drawer Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            color = Color(0xFFF1F8E9) // Very light green background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color(0xFF66BB6A), // Darker light green
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome Back!",
                    color = Color(0xFF1B1B1B), // Dark text
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        menuItems.forEach { item ->
            DrawerMenuItemRow(
                item = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun DrawerMenuItemRow(
    item: DrawerMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Color(0xFF66BB6A), // Darker light green
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B1B1B) // Dark text
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color(0xFF757575) // Light gray
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF66BB6A), // Darker light green
            modifier = Modifier.size(16.dp)
        )
    }
}
