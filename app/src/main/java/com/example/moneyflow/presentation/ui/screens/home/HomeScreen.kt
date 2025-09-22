package com.example.moneyflow.presentation.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyflow.data.model.TransactionType
import com.example.moneyflow.data.model.TransactionWithDetails
import com.example.moneyflow.presentation.ui.theme.AppColors
import com.example.moneyflow.presentation.viewmodel.TransactionViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.Line
import java.text.SimpleDateFormat
import java.util.*

enum class TimeFilter {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    // Reduced responsive dimensions
    val horizontalPadding = if (isTablet) 20.dp else 16.dp
    val verticalSpacing = if (isTablet) 16.dp else 12.dp

    val currentDate = remember {
        SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    // Collect recent transactions
    val recentTransactions by viewModel.transactions.collectAsState()

    // Animation state
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
            vertical = if (isTablet) 16.dp else 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        item {
            // Header Section - Reduced height
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
            // Main Balance Card - Reduced height
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
            // Transaction Graph Section - More compact
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                TransactionGraphSection()
            }
        }

        item {
            // Recent Transactions Section - Compact design
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
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
            .padding(vertical = 2.dp), // Reduced padding
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Good Morning!",
                fontSize = 20.sp, // Reduced from 24sp
                color = AppColors.OnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentDate,
                fontSize = 12.sp, // Reduced from 14sp
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing
        ) {
            // Notification Icon - Smaller
            Surface(
                modifier = Modifier.size(36.dp), // Reduced from 44dp
                shape = RoundedCornerShape(10.dp), // Reduced corner radius
                color = AppColors.Surface,
                shadowElevation = 2.dp, // Reduced elevation
                tonalElevation = 1.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = AppColors.OnSurface,
                        modifier = Modifier.size(18.dp) // Reduced from 22dp
                    )
                }
            }

            // Profile Icon - Smaller
            Surface(
                modifier = Modifier.size(36.dp), // Reduced from 44dp
                shape = RoundedCornerShape(10.dp),
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
                        tint = Color.White,
                        modifier = Modifier.size(18.dp) // Reduced from 22dp
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
                            AppColors.Primary, // Darker light green
                            AppColors.PrimaryVariant, // Darker green
                            AppColors.Secondary  // Medium light green
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
    icon: ImageVector,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp) // Reduced spacing
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = iconColor.copy(alpha = 0.2f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .size(20.dp) // Reduced from 28dp
                    .padding(4.dp)
            )
        }
        Text(
            text = amount,
            color = Color.White,
            fontSize = 12.sp, // Reduced from 14sp
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 9.sp, // Reduced from 11sp
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RecentTransactionsSection(transactions: List<TransactionWithDetails>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // Reduced corner radius
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Reduced elevation
    ) {
        Column(
            modifier = Modifier.padding(18.dp), // Reduced padding
            verticalArrangement = Arrangement.spacedBy(12.dp) // Reduced spacing
        ) {
            // Header - More compact
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium, // Reduced size
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Your latest financial activity",
                        style = MaterialTheme.typography.bodySmall, // Reduced size
                        color = AppColors.TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.Primary.copy(alpha = 0.1f),
                    modifier = Modifier.clickable { /* Navigate to all transactions */ }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), // Reduced padding
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "See All",
                            color = AppColors.Primary,
                            fontSize = 12.sp, // Reduced from 14sp
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "See All",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(14.dp) // Reduced from 16dp
                        )
                    }
                }
            }

            // Transaction Items
            if (transactions.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), // Reduced corner radius
                    colors = CardDefaults.cardColors(containerColor = AppColors.Primary.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp), // Reduced padding
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "No transactions",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(36.dp) // Reduced from 48dp
                        )
                        Text(
                            text = "No recent transactions",
                            color = AppColors.TextSecondary,
                            style = MaterialTheme.typography.titleSmall, // Reduced size
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Your transactions will appear here",
                            color = AppColors.TextSecondary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp) // Reduced spacing
                ) {
                    transactions.forEach { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionWithDetails) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    val (amountColor, backgroundColor, icon) = when (transaction.transaction.type) {
        TransactionType.INCOME -> Triple(
            AppColors.Success,
            AppColors.Success.copy(alpha = 0.1f),
            Icons.Default.Add
        )
        TransactionType.EXPENSE -> Triple(
            AppColors.ExpenseLight,
            AppColors.ExpenseLight.copy(alpha = 0.1f),
            Icons.Default.Delete
        )
        TransactionType.TRANSFER -> Triple(
            AppColors.Primary,
            AppColors.Primary.copy(alpha = 0.1f),
            Icons.Default.ArrowForward
        )
    }

    val amountPrefix = when (transaction.transaction.type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.TRANSFER -> ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Reduced corner radius
        color = AppColors.Background,
        shadowElevation = 1.dp // Reduced elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Reduced padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Category Icon - Smaller
                Surface(
                    modifier = Modifier.size(36.dp), // Reduced from 48dp
                    shape = RoundedCornerShape(10.dp), // Reduced corner radius
                    color = backgroundColor
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = transaction.category?.name,
                            tint = amountColor,
                            modifier = Modifier.size(18.dp) // Reduced from 24dp
                        )
                    }
                }

                // Transaction Details - More compact
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = transaction.transaction.description ?: "Unknown Transaction",
                        fontSize = 14.sp, // Reduced from 16sp
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = transaction.category?.name ?: "Unknown",
                            fontSize = 11.sp, // Reduced from 13sp
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            modifier = Modifier.size(3.dp), // Reduced from 4dp
                            shape = CircleShape,
                            color = AppColors.TextSecondary.copy(alpha = 0.5f)
                        ) {}
                        Text(
                            text = transaction.account?.name ?: "Unknown Account",
                            fontSize = 11.sp, // Reduced from 13sp
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Amount and Date - More compact
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "$amountPrefix${String.format("%.2f", transaction.transaction.amount)}",
                    fontSize = 14.sp, // Reduced from 16sp
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                Text(
                    text = dateFormat.format(transaction.transaction.createdAt),
                    fontSize = 10.sp, // Reduced from 12sp
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TransactionGraphSection() {
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.MONTHLY) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
            initialOffsetY = { it / 3 }
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp), // Reduced corner radius
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Reduced elevation
        ) {
            Column(
                modifier = Modifier.padding(18.dp), // Reduced padding
                verticalArrangement = Arrangement.spacedBy(14.dp) // Reduced spacing
            ) {
                // Header - More compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AppColors.Primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Analytics",
                                    tint = AppColors.Primary,
                                    modifier = Modifier
                                        .size(24.dp) // Reduced from 32dp
                                        .padding(4.dp)
                                )
                            }
                            Text(
                                text = "Transaction Analytics",
                                style = MaterialTheme.typography.titleMedium, // Reduced size
                                fontWeight = FontWeight.Bold,
                                color = AppColors.OnSurface
                            )
                        }
                        Text(
                            text = "Track your spending patterns over time",
                            style = MaterialTheme.typography.bodySmall, // Reduced size
                            color = AppColors.TextSecondary
                        )
                    }

                    // Details Button - Smaller
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        modifier = Modifier.clickable { /* Navigate to details */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), // Reduced padding
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "View Details",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(14.dp) // Reduced from 18dp
                            )
                            Text(
                                text = "Details",
                                color = AppColors.Primary,
                                style = MaterialTheme.typography.bodySmall, // Reduced size
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Time Filter Chips - More compact
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
                ) {
                    TimeFilter.values().forEach { filter ->
                        val isSelected = selectedTimeFilter == filter
                        Surface(
                            shape = RoundedCornerShape(14.dp), // Reduced corner radius
                            color = if (isSelected) AppColors.Primary else Color.Transparent,
                            border = if (!isSelected) BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTimeFilter = filter }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp), // Reduced padding
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when(filter) {
                                        TimeFilter.DAILY -> "Daily"
                                        TimeFilter.WEEKLY -> "Weekly"
                                        TimeFilter.MONTHLY -> "Monthly"
                                        TimeFilter.YEARLY -> "Yearly"
                                    },
                                    color = if (isSelected) Color.White else AppColors.Primary,
                                    style = MaterialTheme.typography.bodySmall, // Reduced size
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Chart - Reduced height
                val chartData = remember(selectedTimeFilter) {
                    generateChartData(selectedTimeFilter)
                }

                Card(
                    shape = RoundedCornerShape(14.dp), // Reduced corner radius
                    colors = CardDefaults.cardColors(containerColor = AppColors.Background)
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Reduced from 260dp
                            .padding(12.dp), // Reduced padding
                        data = listOf(
                            Line(
                                label = "Transactions",
                                values = chartData.values,
                                color = SolidColor(AppColors.Primary),
                                firstGradientFillColor = AppColors.Primary.copy(alpha = 0.4f),
                                secondGradientFillColor = AppColors.Primary.copy(alpha = 0.05f),
                                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                gradientAnimationDelay = 800,
                                drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(2.dp), // Reduced stroke
                                curvedEdges = true
                            )
                        ),
                        curvedEdges = true,
                        animationDelay = 500,
                        labelProperties = ir.ehsannarmani.compose_charts.models.LabelProperties(
                            enabled = true,
                            labels = chartData.labels
                        ),
                        gridProperties = ir.ehsannarmani.compose_charts.models.GridProperties(
                            enabled = true,
                            xAxisProperties = ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties(
                                color = SolidColor(AppColors.Primary.copy(alpha = 0.1f)),
                                thickness = 1.dp
                            ),
                            yAxisProperties = ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties(
                                color = SolidColor(AppColors.Primary.copy(alpha = 0.1f)),
                                thickness = 1.dp
                            )
                        )
                    )
                }

                    }
                }
            }
        }

@Composable
private fun StatsItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp) // Reduced spacing
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.15f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier
                    .size(24.dp) // Reduced from 32dp
                    .padding(4.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium, // Reduced size
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// Data classes for chart
data class ChartData(
    val values: List<Double>,
    val labels: List<String>
)

data class TransactionStats(
    val highest: String,
    val average: String,
    val lowest: String
)

// Generate chart data based on time filter
private fun generateChartData(timeFilter: TimeFilter): ChartData {
    return when (timeFilter) {
        TimeFilter.DAILY -> ChartData(
            values = listOf(120.0, 85.0, 200.0, 150.0, 95.0, 175.0, 220.0),
            labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        )
        TimeFilter.WEEKLY -> ChartData(
            values = listOf(850.0, 920.0, 780.0, 1100.0, 950.0, 1200.0, 890.0, 1050.0),
            labels = listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8")
        )
        TimeFilter.MONTHLY -> ChartData(
            values = listOf(1200.0, 1450.0, 1100.0, 1650.0, 1300.0, 1800.0),
            labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
        )
        TimeFilter.YEARLY -> ChartData(
            values = listOf(18000.0, 19500.0, 20200.0, 21800.0, 22100.0),
            labels = listOf("2020", "2021", "2022", "2023", "2024")
        )
    }
}

// Generate stats based on time filter
private fun generateStats(timeFilter: TimeFilter): TransactionStats {
    return when (timeFilter) {
        TimeFilter.DAILY -> TransactionStats(
            highest = "$220",
            average = "$149",
            lowest = "$85"
        )
        TimeFilter.WEEKLY -> TransactionStats(
            highest = "$1,200",
            average = "$971",
            lowest = "$780"
        )
        TimeFilter.MONTHLY -> TransactionStats(
            highest = "$1,800",
            average = "$1,416",
            lowest = "$1,100"
        )
        TimeFilter.YEARLY -> TransactionStats(
            highest = "$22,100",
            average = "$20,320",
            lowest = "$18,000"
        )
    }
}