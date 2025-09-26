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
import androidx.compose.ui.unit.Dp
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

    // Consistent responsive dimensions
    val horizontalPadding = if (isTablet) 24.dp else 16.dp
    val verticalSpacing = if (isTablet) 20.dp else 16.dp
    val cardPadding = if (isTablet) 24.dp else 20.dp

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
            vertical = if (isTablet) 20.dp else 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        item {
            // Header Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { -it / 2 }
                )
            ) {
                HeaderSection(
                    currentDate = currentDate,
                    isTablet = isTablet
                )
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
                MainBalanceCard(
                    isTablet = isTablet,
                    cardPadding = cardPadding
                )
            }
        }

        item {
            // Transaction Graph Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                TransactionGraphSection(
                    isTablet = isTablet,
                    cardPadding = cardPadding
                )
            }
        }

        item {
            // Recent Transactions Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                RecentTransactionsSection(
                    transactions = recentTransactions.take(5),
                    isTablet = isTablet,
                    cardPadding = cardPadding
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    currentDate: String,
    isTablet: Boolean
) {
    // Consistent typography sizes
    val titleSize = if (isTablet) 22.sp else 20.sp
    val subtitleSize = if (isTablet) 14.sp else 12.sp
    val iconSize = if (isTablet) 40.dp else 36.dp
    val iconInternalSize = if (isTablet) 20.dp else 18.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isTablet) 4.dp else 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Good Morning!",
                fontSize = titleSize,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.Bold,
                lineHeight = titleSize * 1.2
            )
            Text(
                text = currentDate,
                fontSize = subtitleSize,
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.Medium,
                lineHeight = subtitleSize * 1.3
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Notification Icon
            Surface(
                modifier = Modifier.size(iconSize),
                shape = RoundedCornerShape(10.dp),
                color = AppColors.Surface,
                shadowElevation = 2.dp,
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
                        modifier = Modifier.size(iconInternalSize)
                    )
                }
            }

            // Profile Icon
            Surface(
                modifier = Modifier.size(iconSize),
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
                        modifier = Modifier.size(iconInternalSize)
                    )
                }
            }
        }
    }
}

@Composable
fun MainBalanceCard(
    isTablet: Boolean,
    cardPadding: Dp
) {
    // Consistent sizes
    val cardHeight = if (isTablet) 240.dp else 220.dp
    val balanceTextSize = if (isTablet) 36.sp else 32.sp
    val labelTextSize = if (isTablet) 16.sp else 14.sp
    val smallTextSize = if (isTablet) 14.sp else 12.sp
    val iconSize = if (isTablet) 24.dp else 20.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
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
                    .padding(cardPadding),
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
                        fontSize = labelTextSize,
                        fontWeight = FontWeight.Medium,
                        lineHeight = labelTextSize * 1.3
                    )

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Balance",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(iconSize)
                    )
                }

                // Balance amount
                Column {
                    Text(
                        text = "$12,847.50",
                        color = Color.White,
                        fontSize = balanceTextSize,
                        fontWeight = FontWeight.Bold,
                        lineHeight = balanceTextSize * 1.2
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
                            fontSize = smallTextSize,
                            lineHeight = smallTextSize * 1.3
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
                        iconColor = AppColors.Accent,
                        isTablet = isTablet
                    )
                    FinancialItem(
                        label = "Expenses",
                        amount = "-$3,210",
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFFFCDD2),
                        isTablet = isTablet
                    )
                    FinancialItem(
                        label = "Savings",
                        amount = "$2,210",
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFFFE082),
                        isTablet = isTablet
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
    iconColor: Color,
    isTablet: Boolean
) {
    val iconSize = if (isTablet) 24.dp else 20.dp
    val amountTextSize = if (isTablet) 14.sp else 12.sp
    val labelTextSize = if (isTablet) 11.sp else 9.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
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
                    .size(iconSize)
                    .padding(4.dp)
            )
        }
        Text(
            text = amount,
            color = Color.White,
            fontSize = amountTextSize,
            fontWeight = FontWeight.Bold,
            lineHeight = amountTextSize * 1.3
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = labelTextSize,
            fontWeight = FontWeight.Medium,
            lineHeight = labelTextSize * 1.3
        )
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionWithDetails>,
    isTablet: Boolean,
    cardPadding: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Reduced elevation
    ) {
        Column(
            modifier = Modifier.padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
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
                        style = if (isTablet) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Your latest financial activity",
                        style = if (isTablet) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppColors.Primary.copy(alpha = 0.1f),
                    modifier = Modifier.clickable { /* Navigate to all transactions */ }
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isTablet) 16.dp else 12.dp,
                            vertical = if (isTablet) 8.dp else 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "See All",
                            color = AppColors.Primary,
                            fontSize = if (isTablet) 14.sp else 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "See All",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(if (isTablet) 16.dp else 14.dp)
                        )
                    }
                }
            }

            // Transaction Items
            if (transactions.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Primary.copy(alpha = 0.05f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(if (isTablet) 32.dp else 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "No transactions",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(if (isTablet) 48.dp else 36.dp)
                        )
                        Text(
                            text = "No recent transactions",
                            color = AppColors.TextSecondary,
                            style = if (isTablet) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    transactions.forEach { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            isTablet = isTablet
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionWithDetails,
    isTablet: Boolean
) {
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

    // Consistent sizing
    val iconContainerSize = if (isTablet) 44.dp else 36.dp
    val iconSize = if (isTablet) 24.dp else 18.dp
    val titleSize = if (isTablet) 16.sp else 14.sp
    val subtitleSize = if (isTablet) 13.sp else 11.sp
    val amountSize = if (isTablet) 16.sp else 14.sp
    val dateSize = if (isTablet) 12.sp else 10.sp

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.Background,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 16.dp else 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp)
            ) {
                // Category Icon
                Surface(
                    modifier = Modifier.size(iconContainerSize),
                    shape = RoundedCornerShape(10.dp),
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
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }

                // Transaction Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = transaction.transaction.description ?: "Unknown Transaction",
                        fontSize = titleSize,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface,
                        lineHeight = titleSize * 1.3
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = transaction.category?.name ?: "Unknown",
                            fontSize = subtitleSize,
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = subtitleSize * 1.3
                        )
                        Surface(
                            modifier = Modifier.size(3.dp),
                            shape = CircleShape,
                            color = AppColors.TextSecondary.copy(alpha = 0.5f)
                        ) {}
                        Text(
                            text = transaction.account?.name ?: "Unknown Account",
                            fontSize = subtitleSize,
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium,
                            lineHeight = subtitleSize * 1.3
                        )
                    }
                }
            }

            // Amount and Date
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "$amountPrefix${String.format("%.2f", transaction.transaction.amount)}",
                    fontSize = amountSize,
                    fontWeight = FontWeight.Bold,
                    color = amountColor,
                    lineHeight = amountSize * 1.3
                )
                Text(
                    text = dateFormat.format(transaction.transaction.createdAt),
                    fontSize = dateSize,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = dateSize * 1.3
                )
            }
        }
    }
}

@Composable
private fun TransactionGraphSection(
    isTablet: Boolean,
    cardPadding: Dp
) {
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
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Reduced elevation
        ) {
            Column(
                modifier = Modifier.padding(cardPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                        .size(if (isTablet) 32.dp else 24.dp)
                                        .padding(4.dp)
                                )
                            }
                            Text(
                                text = "Transaction Analytics",
                                style = if (isTablet) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.OnSurface
                            )
                        }
                        Text(
                            text = "Track your spending patterns over time",
                            style = if (isTablet) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }

                    // Details Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        modifier = Modifier.clickable { /* Navigate to details */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = if (isTablet) 16.dp else 12.dp,
                                vertical = if (isTablet) 10.dp else 8.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "View Details",
                                tint = AppColors.Primary,
                                modifier = Modifier.size(if (isTablet) 18.dp else 14.dp)
                            )
                            Text(
                                text = "Details",
                                color = AppColors.Primary,
                                style = if (isTablet) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Time Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeFilter.values().forEach { filter ->
                        val isSelected = selectedTimeFilter == filter
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) AppColors.Primary else Color.Transparent,
                            border = if (!isSelected) BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTimeFilter = filter }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = if (isTablet) 12.dp else 10.dp),
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
                                    style = if (isTablet) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Chart
                val chartData = remember(selectedTimeFilter) {
                    generateChartData(selectedTimeFilter)
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Background)
                ) {
                    LineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isTablet) 240.dp else 200.dp)
                            .padding(if (isTablet) 16.dp else 12.dp),
                        data = listOf(
                            Line(
                                label = "Transactions",
                                values = chartData.values,
                                color = SolidColor(AppColors.Primary),
                                firstGradientFillColor = AppColors.Primary.copy(alpha = 0.4f),
                                secondGradientFillColor = AppColors.Primary.copy(alpha = 0.05f),
                                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                gradientAnimationDelay = 800,
                                drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(2.dp),
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
    color: Color,
    isTablet: Boolean
) {
    val iconSize = if (isTablet) 32.dp else 24.dp
    val valueSize = if (isTablet) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
    val titleSize = MaterialTheme.typography.bodySmall

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    .size(iconSize)
                    .padding(4.dp)
            )
        }
        Text(
            text = value,
            style = valueSize,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface
        )
        Text(
            text = title,
            style = titleSize,
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