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

@Composable
fun AnalysisScreen() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Sample data with updated darker color scheme
    val expenseCategories = listOf(
        ExpenseCategory(
            name = "Food & Dining",
            amount = 1250.0,
            percentage = 35f,
            color = AppColors.ExpenseLight, // Darker light red
            icon = { Icon(Icons.Default.Home, null, tint = AppColors.ExpenseLight) }
        ),
        ExpenseCategory(
            name = "Rent & Housing",
            amount = 1800.0,
            percentage = 40f,
            color = AppColors.Primary, // Darker light green
            icon = { Icon(Icons.Default.Home, null, tint = AppColors.Primary) }
        ),
        ExpenseCategory(
            name = "Transportation",
            amount = 450.0,
            percentage = 15f,
            color = AppColors.SavingsLight, // Darker light blue
            icon = { Icon(Icons.Default.CheckCircle, null, tint = AppColors.SavingsLight) }
        ),
        ExpenseCategory(
            name = "Entertainment",
            amount = 300.0,
            percentage = 10f,
            color = AppColors.Warning, // Darker orange
            icon = { Icon(Icons.Default.DateRange, null, tint = AppColors.Warning) }
        )
    )

    val financialSummary = FinancialSummary(
        totalIncome = 5500.0,
        totalExpenses = 3800.0,
        totalSavings = 1700.0,
        monthlyBudget = 4000.0
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = if (isTablet) 24.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (isTablet) 20.dp else 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
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
            // Enhanced Expense Breakdown Chart with all details
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { it / 3 }
                )
            ) {
                EnhancedExpenseBreakdownCard(expenseCategories, isTablet)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnalysisHeader() {
    Column {
        Text(
            text = "Financial Analysis",
            fontSize = 20.sp,
            color = AppColors.Primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Overview of your spending patterns",
            fontSize = 13.sp,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
fun FinancialSummaryHeader() {
    Text(
        text = "Financial Summary",
        fontSize = 18.sp,
        color = AppColors.OnSurface,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun FinancialSummaryCards(summary: FinancialSummary, isTablet: Boolean) {
    val cardHeight = if (isTablet) 110.dp else 100.dp
    val fontSize = if (isTablet) 16.sp else 14.sp
    val subTextSize = if (isTablet) 12.sp else 11.sp

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            SummaryCard(
                title = "Total Income",
                amount = "$${String.format("%.2f", summary.totalIncome)}",
                icon = Icons.Default.ArrowForward,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.Primary, AppColors.Secondary) // Darker light green gradient
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            SummaryCard(
                title = "Total Expenses",
                amount = "$${String.format("%.2f", summary.totalExpenses)}",
                icon = Icons.Default.ArrowBack,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.ExpenseLight, AppColors.Expense) // Darker light red gradient
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            SummaryCard(
                title = "Savings",
                amount = "$${String.format("%.2f", summary.totalSavings)}",
                icon = Icons.Default.ShoppingCart,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.SavingsLight, AppColors.Savings) // Darker light blue gradient
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
            .width(140.dp)
            .height(cardHeight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp)
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
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
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
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp)
        ) {
            Text(
                text = "Expense Breakdown",
                fontSize = if (isTablet) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
            
            Spacer(modifier = Modifier.height(if (isTablet) 24.dp else 20.dp))
            
            if (isTablet) {
                // Tablet layout - side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pie Chart
                    Box(
                        modifier = Modifier.size(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedAnimatedPieChart(categories = categories, isTablet = isTablet)
                    }
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    // Legend
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        categories.forEach { category ->
                            LegendItem(category = category)
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
                        modifier = Modifier.size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedAnimatedPieChart(categories = categories, isTablet = isTablet)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Legend
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        categories.forEach { category ->
                            LegendItem(category = category)
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
        val radius = (size.minDimension / 2) * 0.7f
        val strokeWidth = radius * 0.15f
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

            // Calculate position for labels
            if (animatedProgress.value > 0.5f) {
                val midAngle = startAngle + sweepAngle / 2
                val labelRadius = radius + strokeWidth / 2 + with(density) { 40.dp.toPx() }

                val labelX = center.x + labelRadius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                val labelY = center.y + labelRadius * sin(Math.toRadians(midAngle.toDouble())).toFloat()

                // Draw category name
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = category.color.toArgb()
                        textSize = with(density) { if (isTablet) 14.sp.toPx() else 12.sp.toPx() }
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        isFakeBoldText = true
                    }

                    drawText(
                        category.name,
                        labelX,
                        labelY - with(density) { 8.dp.toPx() },
                        paint
                    )

                    // Draw percentage and amount
                    val detailPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = with(density) { if (isTablet) 12.sp.toPx() else 10.sp.toPx() }
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    drawText(
                        "${category.percentage.toInt()}%",
                        labelX,
                        labelY + with(density) { 8.dp.toPx() },
                        detailPaint
                    )

                    drawText(
                        "$${category.amount.toInt()}",
                        labelX,
                        labelY + with(density) { 24.dp.toPx() },
                        detailPaint
                    )
                }

                // Draw connecting line
                val lineStartRadius = radius + strokeWidth / 2 + with(density) { 8.dp.toPx() }
                val lineEndRadius = radius + strokeWidth / 2 + with(density) { 32.dp.toPx() }

                val lineStartX = center.x + lineStartRadius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                val lineStartY = center.y + lineStartRadius * sin(Math.toRadians(midAngle.toDouble())).toFloat()
                val lineEndX = center.x + lineEndRadius * cos(Math.toRadians(midAngle.toDouble())).toFloat()
                val lineEndY = center.y + lineEndRadius * sin(Math.toRadians(midAngle.toDouble())).toFloat()

                drawLine(
                    color = category.color.copy(alpha = 0.6f),
                    start = androidx.compose.ui.geometry.Offset(lineStartX, lineStartY),
                    end = androidx.compose.ui.geometry.Offset(lineEndX, lineEndY),
                    strokeWidth = with(density) { 2.dp.toPx() }
                )
            }

            startAngle += sweepAngle
        }

        // Draw center circle with total
        drawCircle(
            color = Color.White,
            radius = radius * 0.4f,
            center = center
        )

        drawCircle(
            color = AppColors.Primary.copy(alpha = 0.1f),
            radius = radius * 0.35f,
            center = center
        )

        // Draw center text
        if (animatedProgress.value > 0.3f) {
            drawContext.canvas.nativeCanvas.apply {
                val totalPaint = android.graphics.Paint().apply {
                    color = AppColors.Primary.toArgb()
                    textSize = with(density) { if (isTablet) 18.sp.toPx() else 16.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText = true
                }

                val labelPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = with(density) { if (isTablet) 14.sp.toPx() else 12.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                drawText(
                    "Total",
                    center.x,
                    center.y - with(density) { 12.dp.toPx() },
                    labelPaint
                )

                drawText(
                    "$${categories.sumOf { it.amount }.toInt()}",
                    center.x,
                    center.y + with(density) { 8.dp.toPx() },
                    totalPaint
                )
            }
        }
    }
}

@Composable
fun LegendItem(category: ExpenseCategory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(category.color, CircleShape)
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.name,
                fontSize = 14.sp,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$${String.format("%.0f", category.amount)}",
                fontSize = 12.sp,
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.Normal
            )
        }
        
        Text(
            text = "${category.percentage.toInt()}%",
            fontSize = 14.sp,
            color = category.color,
            fontWeight = FontWeight.Bold
        )
    }
}


