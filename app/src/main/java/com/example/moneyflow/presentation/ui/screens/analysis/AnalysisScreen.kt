package com.example.moneyflow.presentation.ui.screens.analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyflow.R
import com.example.moneyflow.presentation.ui.theme.AppColors
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutCubic
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import com.example.moneyflow.presentation.ui.screens.home.TimeFilter
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

// Sample data classes for demonstration
data class ExpenseCategory(
    val name: String,
    val amount: Double,
    val percentage: Float,
    val color: Color,
    val icon: @Composable () -> Unit
)

data class FinancialSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val totalSavings: Double,
    val monthlyBudget: Double
)

data class MonthlyData(
    val month: String,
    val income: Double,
    val expense: Double,
    val savings: Double
)

data class BudgetItem(
    val category: String,
    val budgeted: Double,
    val actual: Double,
    val color: Color
)

data class TopTransaction(
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val color: Color
)

enum class ChartType {
    EXPENSE_BREAKDOWN,
    INCOME_VS_EXPENSE,
    BUDGET_VS_ACTUAL,
    SAVINGS_TREND,
    TOP_TRANSACTIONS
}

@Composable
fun AnalysisScreen() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    var isVisible by remember { mutableStateOf(false) }
    var selectedChart by remember { mutableStateOf(ChartType.EXPENSE_BREAKDOWN) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Sample data with updated darker color scheme
    val expenseCategories = listOf(
        ExpenseCategory(
            name = "Food & Dining",
            amount = 1250.0,
            percentage = 35f,
            color = AppColors.ExpenseLight,
            icon = { Icon(Icons.Default.AccountBox, null, tint = AppColors.ExpenseLight) }
        ),
        ExpenseCategory(
            name = "Rent & Housing",
            amount = 1800.0,
            percentage = 40f,
            color = AppColors.Primary,
            icon = { Icon(Icons.Default.Home, null, tint = AppColors.Primary) }
        ),
        ExpenseCategory(
            name = "Transportation",
            amount = 450.0,
            percentage = 15f,
            color = AppColors.SavingsLight,
            icon = { Icon(Icons.Default.ArrowForward, null, tint = AppColors.SavingsLight) }
        ),
        ExpenseCategory(
            name = "Entertainment",
            amount = 300.0,
            percentage = 10f,
            color = AppColors.Warning,
            icon = { Icon(Icons.Default.Star, null, tint = AppColors.Warning) }
        )
    )

    val financialSummary = FinancialSummary(
        totalIncome = 5500.0,
        totalExpenses = 3800.0,
        totalSavings = 1700.0,
        monthlyBudget = 4000.0
    )

    // Sample monthly data for line chart
    val monthlyData = listOf(
        MonthlyData("Jan", 5200.0, 3600.0, 1600.0),
        MonthlyData("Feb", 5400.0, 3800.0, 1600.0),
        MonthlyData("Mar", 5300.0, 3700.0, 1600.0),
        MonthlyData("Apr", 5600.0, 4000.0, 1600.0),
        MonthlyData("May", 5500.0, 3800.0, 1700.0),
        MonthlyData("Jun", 5700.0, 3900.0, 1800.0)
    )

    // Sample budget data
    val budgetData = listOf(
        BudgetItem("Food", 1200.0, 1250.0, AppColors.ExpenseLight),
        BudgetItem("Rent", 1800.0, 1800.0, AppColors.Primary),
        BudgetItem("Transport", 500.0, 450.0, AppColors.SavingsLight),
        BudgetItem("Entertainment", 250.0, 300.0, AppColors.Warning),
        BudgetItem("Others", 250.0, 200.0, Color(0xFF9C27B0))
    )

    // Sample top transactions
    val topTransactions = listOf(
        TopTransaction("Monthly Rent Payment", 1800.0, "Rent", "Jun 1", AppColors.Primary),
        TopTransaction("Grocery Shopping - Walmart", 245.0, "Food", "Jun 15", AppColors.ExpenseLight),
        TopTransaction("Gas Station Fill-up", 85.0, "Transport", "Jun 10", AppColors.SavingsLight),
        TopTransaction("Netflix Subscription", 15.99, "Entertainment", "Jun 1", AppColors.Warning),
        TopTransaction("Dinner at Restaurant", 120.0, "Food", "Jun 20", AppColors.ExpenseLight)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = if (isTablet) 20.dp else 14.dp),
        verticalArrangement = Arrangement.spacedBy(if (isTablet) 14.dp else 10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            // Header Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { -it / 2 }
                )
            ) {
                AnalysisHeader()
            }
        }

        item {
            // Financial Summary Header
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                FinancialSummaryHeader()
            }
        }

        item {
            // Financial Summary Cards
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                FinancialSummaryCards(financialSummary, isTablet)
            }
        }

        item {
            // Chart Selection Buttons
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                ChartSelectionButtons(
                    selectedChart = selectedChart,
                    onChartSelected = { selectedChart = it },
                    isTablet = isTablet
                )
            }
        }

        item {
            // Dynamic Chart Display
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                Box(
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    when (selectedChart) {
                        ChartType.EXPENSE_BREAKDOWN -> {
                            EnhancedExpenseBreakdownCard(expenseCategories, isTablet)
                        }
                        ChartType.INCOME_VS_EXPENSE -> {
                            IncomeVsExpenseCard(monthlyData, isTablet)
                        }
                        ChartType.BUDGET_VS_ACTUAL -> {
                            BudgetVsActualCard(budgetData, isTablet)
                        }
                        ChartType.SAVINGS_TREND -> {
                            SavingsTrendCard(monthlyData, isTablet)
                        }
                        ChartType.TOP_TRANSACTIONS -> {
                            TopTransactionsCard(topTransactions, isTablet)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChartSelectionButtons(
    selectedChart: ChartType,
    onChartSelected: (ChartType) -> Unit,
    isTablet: Boolean
) {
    Column {
        Text(
            text = "Analytics",
            fontSize = if (isTablet) 18.sp else 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            item {
                ChartButton(
                    title = "Expenses",
                    icon = Icons.Default.Star,
                    isSelected = selectedChart == ChartType.EXPENSE_BREAKDOWN,
                    onClick = { onChartSelected(ChartType.EXPENSE_BREAKDOWN) },
                    isTablet = isTablet
                )
            }
            item {
                ChartButton(
                    title = "Trends",
                    icon = Icons.Default.Star,
                    isSelected = selectedChart == ChartType.INCOME_VS_EXPENSE,
                    onClick = { onChartSelected(ChartType.INCOME_VS_EXPENSE) },
                    isTablet = isTablet
                )
            }
            item {
                ChartButton(
                    title = "Budget",
                    icon = Icons.Default.Star,
                    isSelected = selectedChart == ChartType.BUDGET_VS_ACTUAL,
                    onClick = { onChartSelected(ChartType.BUDGET_VS_ACTUAL) },
                    isTablet = isTablet
                )
            }
            item {
                ChartButton(
                    title = "Savings",
                    icon = Icons.Default.Star,
                    isSelected = selectedChart == ChartType.SAVINGS_TREND,
                    onClick = { onChartSelected(ChartType.SAVINGS_TREND) },
                    isTablet = isTablet
                )
            }
            item {
                ChartButton(
                    title = "Top Spends",
                    icon = Icons.Default.Star,
                    isSelected = selectedChart == ChartType.TOP_TRANSACTIONS,
                    onClick = { onChartSelected(ChartType.TOP_TRANSACTIONS) },
                    isTablet = isTablet
                )
            }
        }
    }
}

@Composable
fun ChartButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isTablet: Boolean
) {
    val buttonWidth = if (isTablet) 105.dp else 88.dp
    val buttonHeight = if (isTablet) 56.dp else 50.dp

    Card(
        modifier = Modifier
            .width(buttonWidth)
            .height(buttonHeight),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.Primary else Color.White
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else AppColors.Primary,
                modifier = Modifier.size(if (isTablet) 22.dp else 18.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = title,
                fontSize = if (isTablet) 11.sp else 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else AppColors.OnSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun IncomeVsExpenseCard(data: List<MonthlyData>, isTablet: Boolean) {
    // Time Filter Chips - More compact
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.MONTHLY) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 18.dp else 14.dp)
        ) {
            Text(
                text = "Income vs Expense Trends",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TimeFilter.values().forEach { filter ->
                    val isSelected = selectedTimeFilter == filter
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AppColors.Primary else Color.Transparent,
                        border = if (!isSelected) BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTimeFilter = filter }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
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
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = if (isTablet) 12.sp else 10.sp
                                ),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chart
            val chartData = remember(selectedTimeFilter) {
                val filtered = when(selectedTimeFilter) {
                    TimeFilter.DAILY, TimeFilter.WEEKLY, TimeFilter.MONTHLY, TimeFilter.YEARLY -> data
                }
                object {
                    val labels = filtered.map { it.month }
                    val incomeValues = filtered.map { it.income }
                    val expenseValues = filtered.map { it.expense }
                }
            }

            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 220.dp else 180.dp)
                    .padding(8.dp),
                data = listOf(
                    Line(
                        label = "Income",
                        values = chartData.incomeValues,
                        color = SolidColor(AppColors.Primary),
                        firstGradientFillColor = AppColors.Primary.copy(alpha = 0.3f),
                        secondGradientFillColor = AppColors.Primary.copy(alpha = 0.03f),
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 800,
                        drawStyle = DrawStyle.Stroke(3.dp),
                        curvedEdges = true
                    ),
                    Line(
                        label = "Expense",
                        values = chartData.expenseValues,
                        color = SolidColor(AppColors.ExpenseLight),
                        firstGradientFillColor = AppColors.ExpenseLight.copy(alpha = 0.3f),
                        secondGradientFillColor = AppColors.ExpenseLight.copy(alpha = 0.03f),
                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                        gradientAnimationDelay = 800,
                        drawStyle = ir.ehsannarmani.compose_charts.models.DrawStyle.Stroke(3.dp),
                        curvedEdges = true
                    )
                ),
                curvedEdges = true,
                animationDelay = 500,
                labelProperties = ir.ehsannarmani.compose_charts.models.LabelProperties(
                    enabled = true,
                    labels = chartData.labels,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = if (isTablet) 12.sp else 10.sp
                    )
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

            Spacer(modifier = Modifier.height(8.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItemHorizontal("Income", AppColors.Primary, isTablet)
                LegendItemHorizontal("Expense", AppColors.ExpenseLight, isTablet)
            }
        }
    }
}

@Composable
fun BudgetVsActualCard(budgetItems: List<BudgetItem>, isTablet: Boolean) {
    var animationProgress by remember { mutableStateOf(0f) }

    val animatedProgress = animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1200),
        label = "budget_animation"
    )

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 18.dp else 14.dp)
        ) {
            Text(
                text = "Budget vs Actual Spending",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            budgetItems.forEachIndexed { index, budgetItem ->
                AnimatedVisibility(
                    visible = animatedProgress.value > (index * 0.2f),
                    enter = fadeIn(tween(500, delayMillis = index * 100)) +
                            slideInVertically(tween(500, delayMillis = index * 100))
                ) {
                    BudgetBarItem(
                        item = budgetItem,
                        progress = animatedProgress.value,
                        isTablet = isTablet
                    )
                }

                if (index < budgetItems.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BudgetBarItem(item: BudgetItem, progress: Float, isTablet: Boolean) {
    val maxAmount = maxOf(item.budgeted, item.actual)
    val budgetPercentage = (item.budgeted / maxAmount) * progress
    val actualPercentage = (item.actual / maxAmount) * progress

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.category,
                fontSize = if (isTablet) 14.sp else 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )
            Text(
                text = "$${item.actual.toInt()} / $${item.budgeted.toInt()}",
                fontSize = if (isTablet) 13.sp else 12.sp,
                color = if (item.actual > item.budgeted) AppColors.ExpenseLight else AppColors.Primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTablet) 20.dp else 16.dp)
                .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
        ) {
            // Budget bar (background)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(budgetPercentage.toFloat())
                    .background(
                        item.color.copy(alpha = 0.25f),
                        RoundedCornerShape(8.dp)
                    )
            )

            // Actual bar (foreground)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(actualPercentage.toFloat())
                    .background(
                        item.color,
                        RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}

@Composable
fun LegendItemHorizontal(label: String, color: Color, isTablet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isTablet) 14.dp else 12.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = if (isTablet) 13.sp else 12.sp,
            color = AppColors.OnSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AnalysisHeader() {
    Column {
        Text(
            text = "Financial Analysis",
            fontSize = 18.sp,
            color = AppColors.Primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Overview of your spending patterns",
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
fun FinancialSummaryHeader() {
    Text(
        text = "Financial Summary",
        fontSize = 16.sp,
        color = AppColors.OnSurface,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun FinancialSummaryCards(summary: FinancialSummary, isTablet: Boolean) {
    val cardHeight = if (isTablet) 90.dp else 80.dp
    val fontSize = if (isTablet) 14.sp else 13.sp
    val subTextSize = if (isTablet) 11.sp else 10.sp

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        item {
            SummaryCard(
                title = "Total Income",
                amount = "${String.format("%.0f", summary.totalIncome)}",
                icon = Icons.Default.ArrowForward,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.Primary, AppColors.Secondary)
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            SummaryCard(
                title = "Total Expenses",
                amount = "${String.format("%.0f", summary.totalExpenses)}",
                icon = Icons.Default.ArrowBack,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.ExpenseLight, AppColors.Expense)
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            SummaryCard(
                title = "Savings",
                amount = "${String.format("%.0f", summary.totalSavings)}",
                icon = Icons.Default.Star,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.SavingsLight, AppColors.Savings)
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    cardHeight: Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    subTextSize: androidx.compose.ui.unit.TextUnit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(cardHeight),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = subTextSize,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EnhancedExpenseBreakdownCard(categories: List<ExpenseCategory>, isTablet: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 18.dp else 14.dp)
        ) {
            Text(
                text = "Expense Breakdown",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isTablet) {
                // Tablet layout - side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pie Chart
                    Box(
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedAnimatedPieChart(categories = categories, isTablet = isTablet)
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Legend
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { category ->
                            LegendItem(category = category, isTablet = isTablet)
                        }
                    }
                }
            } else {
                // Mobile layout - stacked
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pie Chart
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedAnimatedPieChart(categories = categories, isTablet = isTablet)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Legend
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        categories.forEach { category ->
                            LegendItem(category = category, isTablet = isTablet)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedAnimatedPieChart(categories: List<ExpenseCategory>, isTablet: Boolean) {
    var animationProgress by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val animatedProgress = animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000),
        label = "pie_animation"
    )

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = (size.minDimension / 2) * 0.65f
        val strokeWidth = radius * 0.18f
        val center = androidx.compose.ui.geometry.Offset(canvasWidth / 2, canvasHeight / 2)

        var startAngle = -90f

        categories.forEach { category ->
            val sweepAngle = (category.percentage / 100f) * 360f * animatedProgress.value

            // Draw the arc
            drawArc(
                color = category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = androidx.compose.ui.geometry.Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }

        // Draw center circle with total
        drawCircle(
            color = Color.White,
            radius = radius * 0.38f,
            center = center
        )

        drawCircle(
            color = AppColors.Primary.copy(alpha = 0.08f),
            radius = radius * 0.33f,
            center = center
        )

        // Draw center text
        if (animatedProgress.value > 0.3f) {
            drawContext.canvas.nativeCanvas.apply {
                val totalPaint = android.graphics.Paint().apply {
                    color = AppColors.Primary.toArgb()
                    textSize = with(density) { if (isTablet) 16.sp.toPx() else 14.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText = true
                }

                val labelPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = with(density) { if (isTablet) 12.sp.toPx() else 11.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                drawText(
                    "Total",
                    center.x,
                    center.y - with(density) { 8.dp.toPx() },
                    labelPaint
                )

                drawText(
                    "${categories.sumOf { it.amount }.toInt()}",
                    center.x,
                    center.y + with(density) { 8.dp.toPx() },
                    totalPaint
                )
            }
        }
    }
}

@Composable
fun LegendItem(category: ExpenseCategory, isTablet: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isTablet) 14.dp else 12.dp)
                .background(category.color, CircleShape)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.name,
                fontSize = if (isTablet) 13.sp else 12.sp,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${String.format("%.0f", category.amount)}",
                fontSize = if (isTablet) 11.sp else 10.sp,
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.Normal
            )
        }

        Text(
            text = "${category.percentage.toInt()}%",
            fontSize = if (isTablet) 13.sp else 12.sp,
            color = category.color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SavingsTrendCard(data: List<MonthlyData>, isTablet: Boolean) {
    var animationProgress by remember { mutableStateOf(0f) }

    val animatedProgress = animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1500),
        label = "savings_animation"
    )

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 18.dp else 14.dp)
        ) {
            Text(
                text = "Savings Over Time",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Area Chart for savings
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isTablet) 240.dp else 200.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val padding = 60f
                val chartWidth = canvasWidth - (padding * 2)
                val chartHeight = canvasHeight - (padding * 2)

                val maxValue = data.maxOf { it.savings }
                val stepX = chartWidth / (data.size - 1)

                // Create path for area fill
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(padding, padding + chartHeight)

                data.forEachIndexed { index, monthData ->
                    val x = padding + (index * stepX)
                    val y = padding + chartHeight - ((monthData.savings / maxValue) * chartHeight * animatedProgress.value).toFloat()
                    path.lineTo(x, y)
                }

                path.lineTo(padding + chartWidth, padding + chartHeight)
                path.close()

                // Draw area fill
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.SavingsLight.copy(alpha = 0.5f),
                            AppColors.SavingsLight.copy(alpha = 0.08f)
                        )
                    )
                )

                // Draw line
                for (i in 0 until data.size - 1) {
                    val x1 = padding + (i * stepX)
                    val y1 = padding + chartHeight - ((data[i].savings / maxValue) * chartHeight * animatedProgress.value).toFloat()
                    val x2 = padding + ((i + 1) * stepX)
                    val y2 = padding + chartHeight - ((data[i + 1].savings / maxValue) * chartHeight * animatedProgress.value).toFloat()

                    drawLine(
                        color = AppColors.SavingsLight,
                        start = androidx.compose.ui.geometry.Offset(x1, y1),
                        end = androidx.compose.ui.geometry.Offset(x2, y2),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Draw points
                data.forEachIndexed { index, monthData ->
                    val x = padding + (index * stepX)
                    val y = padding + chartHeight - ((monthData.savings / maxValue) * chartHeight * animatedProgress.value).toFloat()

                    drawCircle(
                        color = AppColors.SavingsLight,
                        radius = 5.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.5.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }

                // Draw month labels
                data.forEachIndexed { index, monthData ->
                    val x = padding + (index * stepX)
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = Color.Gray.toArgb()
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        drawText(
                            monthData.month,
                            x,
                            canvasHeight - 15f,
                            paint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Avg: ${data.map { it.savings }.average().toInt()}",
                    fontSize = if (isTablet) 14.sp else 13.sp,
                    color = AppColors.SavingsLight,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total: ${data.sumOf { it.savings }.toInt()}",
                    fontSize = if (isTablet) 14.sp else 13.sp,
                    color = AppColors.SavingsLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TopTransactionsCard(transactions: List<TopTransaction>, isTablet: Boolean) {
    var animationProgress by remember { mutableStateOf(0f) }

    val animatedProgress = animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "transactions_animation"
    )

    LaunchedEffect(Unit) {
        animationProgress = 1f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 18.dp else 14.dp)
        ) {
            Text(
                text = "Top 5 Expense Transactions",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val maxAmount = transactions.maxOf { it.amount }

            transactions.forEachIndexed { index, transaction ->
                AnimatedVisibility(
                    visible = animatedProgress.value > (index * 0.2f),
                    enter = fadeIn(tween(500, delayMillis = index * 100)) +
                            slideInVertically(tween(500, delayMillis = index * 100))
                ) {
                    TransactionItem(
                        transaction = transaction,
                        maxAmount = maxAmount,
                        progress = animatedProgress.value,
                        isTablet = isTablet
                    )
                }

                if (index < transactions.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TopTransaction,
    maxAmount: Double,
    progress: Float,
    isTablet: Boolean
) {
    val barProgress = (transaction.amount / maxAmount) * progress

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    fontSize = if (isTablet) 14.sp else 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.category,
                        fontSize = if (isTablet) 12.sp else 11.sp,
                        color = transaction.color,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${transaction.date}",
                        fontSize = if (isTablet) 11.sp else 10.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }

            Text(
                text = "${String.format("%.2f", transaction.amount)}",
                fontSize = if (isTablet) 16.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color = transaction.color
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.Gray.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barProgress.toFloat())
                    .background(
                        transaction.color.copy(alpha = 0.8f),
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}