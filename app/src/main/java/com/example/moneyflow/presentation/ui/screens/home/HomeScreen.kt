package com.example.moneyflow.presentation.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyflow.data.model.TransactionType
import com.example.moneyflow.data.model.TransactionWithDetails
import com.example.moneyflow.presentation.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

// Consistent Color Palette
object AppColors {
    val Primary = Color(0xFF2E7D32) // Dark green
    val PrimaryVariant = Color(0xFF388E3C) // Deep green
    val Secondary = Color(0xFF4CAF50) // Medium green
    val SecondaryLight = Color(0xFF66BB6A) // Light green
    val Accent = Color(0xFFA8E6CF) // Light mint green
    val Background = Color(0xFFF5F8F5) // Light neutral background
    val Surface = Color.White
    val OnSurface = Color(0xFF1B1B1B)
    val TextSecondary = Color(0xFF5F6368)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    // Responsive dimensions
    val horizontalPadding = if (isTablet) 24.dp else 16.dp
    val titleFontSize = if (isTablet) 22.sp else 18.sp
    val bodyFontSize = if (isTablet) 16.sp else 14.sp

    val currentDate = remember {
        SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    // Collect recent transactions
    val recentTransactions by viewModel.transactions.collectAsState()

    // Animation states
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = if (isTablet) 20.dp else 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(if (isTablet) 20.dp else 16.dp)
    ) {
        item {
            // Header Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { -it / 2 }
                )
            ) {
                HeaderSection(currentDate = currentDate)
            }
        }

        item {
            // Main Balance Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                MainBalanceCard()
            }
        }

        item {
            // Recent Transactions Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                RecentTransactionsSection(transactions = recentTransactions.take(5))
            }
        }
    }
}

@Composable
fun HeaderSection(currentDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning!",
                fontSize = 20.sp,
                color = AppColors.Primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentDate,
                fontSize = 13.sp,
                color = AppColors.TextSecondary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Notification Icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AppColors.Surface,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = AppColors.PrimaryVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Profile Icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AppColors.Primary,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = AppColors.Surface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MainBalanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppColors.Primary,
                            AppColors.PrimaryVariant,
                            AppColors.Secondary
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Balance",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = Icons.Default.Star ,
                        contentDescription = "Balance",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Balance amount
                Column {
                    Text(
                        text = "$12,847.50",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Growth",
                            tint = AppColors.Accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "+2.5% from last month",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Financial Overview Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FinancialItem(
                        label = "Income",
                        amount = "+$5,420",
                        icon = Icons.Default.Star,
                        iconColor = AppColors.Accent
                    )
                    FinancialItem(
                        label = "Expenses",
                        amount = "-$3,210",
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFFFCDD2)
                    )
                    FinancialItem(
                        label = "Savings",
                        amount = "$2,210",
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFFFE082)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialItem(
    label: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
        Text(
            text = amount,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RecentTransactionsSection(transactions: List<TransactionWithDetails>) {
    Column {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
            
            TextButton(
                onClick = { /* Navigate to all transactions */ }
            ) {
                Text(
                    text = "See All",
                    color = AppColors.Primary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Transactions List
        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "No transactions",
                            modifier = Modifier.size(48.dp),
                            tint = AppColors.TextSecondary.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No transactions yet",
                            color = AppColors.TextSecondary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tap the + button to add your first transaction",
                            color = AppColors.TextSecondary.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    transactions.forEachIndexed { index, transaction ->
                        TransactionItem(transaction = transaction)
                        if (index < transactions.size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = AppColors.TextSecondary.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionWithDetails) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val amountColor = when (transaction.transaction.type) {
        TransactionType.INCOME -> Color(0xFF4CAF50)
        TransactionType.EXPENSE -> Color(0xFFFF5252)
        TransactionType.TRANSFER -> TODO()
    }
    
    val amountPrefix = when (transaction.transaction.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.TRANSFER -> TODO()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Icon
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = when (transaction.transaction.type) {
                    TransactionType.INCOME -> Color(0xFFE8F5E8)
                    TransactionType.EXPENSE -> Color(0xFFFFEBEE)
                    TransactionType.TRANSFER -> TODO()
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (transaction.transaction.type) {
                            TransactionType.INCOME -> Icons.Default.ArrowForward
                            TransactionType.EXPENSE -> Icons.Default.ArrowBack
                            TransactionType.TRANSFER -> TODO()
                        },
                        contentDescription = transaction.category?.name,
                        tint = amountColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Transaction Details
            Column {
                Text(
                    text = transaction.transaction.description ?: "Unknown Transaction",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = transaction.category?.name ?: "Unknown",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        text = transaction.account?.name ?: "Unknown Account",
                        fontSize = 12.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }

        // Amount and Date
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$amountPrefix$${String.format("%.2f", transaction.transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Text(
                text = dateFormat.format(transaction.transaction.createdAt),
                fontSize = 12.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}

