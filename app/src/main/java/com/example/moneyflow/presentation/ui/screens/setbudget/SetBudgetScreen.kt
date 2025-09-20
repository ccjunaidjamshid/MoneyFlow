package com.example.moneyflow.presentation.ui.screens.setbudget

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// Beautiful Light Green Color Palette - Same as other screens
object AppColors {
    val Primary = Color(0xFF81C784) // Light green
    val PrimaryVariant = Color(0xFF66BB6A) // Slightly darker light green
    val Secondary = Color(0xFFA5D6A7) // Very light green
    val SecondaryLight = Color(0xFFC8E6C9) // Ultra light green
    val Accent = Color(0xFFC8E6C9) // Very light green accent
    val Background = Color.White // Pure white background
    val Surface = Color.White
    val OnSurface = Color(0xFF1B1B1B)
    val TextSecondary = Color(0xFF5F6368)
    val Error = Color(0xFFEF9A9A) // Light red for errors
    val Success = Color(0xFF81C784) // Light green for success
    val Warning = Color(0xFFFFCC80) // Light orange for warning
    val ExpenseLight = Color(0xFFFFAB91) // Light orange for expenses
    val Expense = Color(0xFFFF8A65) // Darker orange
    val SavingsLight = Color(0xFF81D4FA) // Light blue
    val Savings = Color(0xFF4FC3F7) // Darker blue
    val CardBackground = Color(0xFFF5F5F5)
}

// Data classes for budget management
data class BudgetCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val budgetAmount: Double,
    val spentAmount: Double,
    val color: Color,
    val isEditing: Boolean = false
) {
    val remainingAmount: Double get() = budgetAmount - spentAmount
    val progressPercentage: Float get() = if (budgetAmount > 0) (spentAmount / budgetAmount).toFloat() else 0f
    val isOverBudget: Boolean get() = spentAmount > budgetAmount
}

data class BudgetSummary(
    val totalBudget: Double,
    val totalSpent: Double,
    val totalRemaining: Double,
    val overBudgetCategories: Int,
    val onTrackCategories: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBudgetScreen() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600

    var isVisible by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Sample budget data - in real app this would come from ViewModel
    var budgetCategories by remember {
        mutableStateOf(
            listOf(
                BudgetCategory(
                    id = "food",
                    name = "Food & Dining",
                    icon = Icons.Default.Home,
                    budgetAmount = 800.0,
                    spentAmount = 650.0,
                    color = AppColors.ExpenseLight
                ),
                BudgetCategory(
                    id = "transport",
                    name = "Transportation",
                    icon = Icons.Default.Star,
                    budgetAmount = 300.0,
                    spentAmount = 420.0, // Over budget
                    color = AppColors.Warning
                ),
                BudgetCategory(
                    id = "entertainment",
                    name = "Entertainment",
                    icon = Icons.Default.Face,
                    budgetAmount = 200.0,
                    spentAmount = 85.0,
                    color = AppColors.SavingsLight
                ),
                BudgetCategory(
                    id = "shopping",
                    name = "Shopping",
                    icon = Icons.Default.ShoppingCart,
                    budgetAmount = 500.0,
                    spentAmount = 280.0,
                    color = AppColors.Primary
                ),
                BudgetCategory(
                    id = "health",
                    name = "Healthcare",
                    icon = Icons.Default.Favorite,
                    budgetAmount = 400.0,
                    spentAmount = 120.0,
                    color = AppColors.Success
                )
            )
        )
    }

    val budgetSummary = BudgetSummary(
        totalBudget = budgetCategories.sumOf { it.budgetAmount },
        totalSpent = budgetCategories.sumOf { it.spentAmount },
        totalRemaining = budgetCategories.sumOf { it.remainingAmount },
        overBudgetCategories = budgetCategories.count { it.isOverBudget },
        onTrackCategories = budgetCategories.count { !it.isOverBudget }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header with Create Budget Button - Same style as Account Screen
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + slideInVertically(
                initialOffsetY = { -it / 2 }
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp,
                color = AppColors.Surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Budget Manager",
                            fontSize = if (isTablet) 26.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Primary
                        )
                        Text(
                            text = "Total Budget: $${String.format("%.0f", budgetSummary.totalBudget)}",
                            fontSize = if (isTablet) 16.sp else 14.sp,
                            color = AppColors.Secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${budgetCategories.size} budget${if (budgetCategories.size != 1) "s" else ""}",
                            fontSize = if (isTablet) 13.sp else 11.sp,
                            color = AppColors.TextSecondary
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddBudgetDialog = true },
                        modifier = Modifier.size(if (isTablet) 56.dp else 48.dp),
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.Surface,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Budget",
                            modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp)
        ) {
            // Budget Overview Cards
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    BudgetOverviewCards(
                        summary = budgetSummary,
                        isTablet = isTablet
                    )
                }
            }

            // Visual Budget Progress Chart
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400)) + slideInVertically(
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    BudgetProgressChart(
                        summary = budgetSummary,
                        categories = budgetCategories,
                        isTablet = isTablet
                    )
                }
            }

            // Alert for over-budget categories
            if (budgetSummary.overBudgetCategories > 0) {
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(800, delayMillis = 300))
                    ) {
                        BudgetAlertCard(overBudgetCount = budgetSummary.overBudgetCategories)
                    }
                }
            }

            // Budget Categories
            items(budgetCategories) { category ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 600)) + slideInVertically(
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    EnhancedBudgetCategoryCard(
                        category = category,
                        isTablet = isTablet,
                        onBudgetChange = { newAmount ->
                            budgetCategories = budgetCategories.map {
                                if (it.id == category.id) it.copy(budgetAmount = newAmount)
                                else it
                            }
                        }
                    )
                }
            }

            // Empty state or add more button
            if (budgetCategories.isEmpty()) {
                item {
                    EmptyBudgetState(
                        isTablet = isTablet,
                        onAddClick = { showAddBudgetDialog = true }
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Budget Dialog
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onDismiss = { showAddBudgetDialog = false },
            onSave = { name, amount, icon, color ->
                // Add new budget category
                val newCategory = BudgetCategory(
                    id = name.lowercase().replace(" ", "_"),
                    name = name,
                    icon = icon,
                    budgetAmount = amount,
                    spentAmount = 0.0,
                    color = color
                )
                budgetCategories = budgetCategories + newCategory
                showAddBudgetDialog = false
            }
        )
    }
}

@Composable
fun BudgetOverviewCards(
    summary: BudgetSummary,
    isTablet: Boolean
) {
    val cardHeight = if (isTablet) 110.dp else 100.dp
    val fontSize = if (isTablet) 16.sp else 14.sp
    val subTextSize = if (isTablet) 12.sp else 11.sp

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            OverviewCard(
                title = "Total Budget",
                amount = "$${String.format("%.0f", summary.totalBudget)}",
                icon = Icons.Default.AccountCircle,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.Primary, AppColors.Secondary)
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            OverviewCard(
                title = "Total Spent",
                amount = "$${String.format("%.0f", summary.totalSpent)}",
                icon = Icons.Default.ArrowForward,
                gradient = Brush.linearGradient(
                    colors = listOf(AppColors.ExpenseLight, AppColors.Expense)
                ),
                cardHeight = cardHeight,
                fontSize = fontSize,
                subTextSize = subTextSize
            )
        }
        item {
            OverviewCard(
                title = "Remaining",
                amount = "$${String.format("%.0f", summary.totalRemaining)}",
                icon = Icons.Default.ArrowBack,
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
fun OverviewCard(
    title: String,
    amount: String,
    icon: ImageVector,
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
fun BudgetProgressChart(
    summary: BudgetSummary,
    categories: List<BudgetCategory>,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(if (isTablet) 24.dp else 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget Overview",
                    fontSize = if (isTablet) 20.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )

                // Progress indicator
                val overallProgress = if (summary.totalBudget > 0)
                    (summary.totalSpent / summary.totalBudget).toFloat() else 0f

                Surface(
                    color = if (overallProgress > 1f) AppColors.Error.copy(alpha = 0.1f)
                    else AppColors.Success.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${(overallProgress * 100).roundToInt()}% Used",
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (overallProgress > 1f) AppColors.Error else AppColors.Success,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall progress bar
            val animatedOverallProgress by animateFloatAsState(
                targetValue = (summary.totalSpent / summary.totalBudget).toFloat().coerceAtMost(1f),
                animationSpec = tween(1200, delayMillis = 300),
                label = "overall_progress"
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Overall Progress",
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$${String.format("%.0f", summary.totalSpent)} / $${String.format("%.0f", summary.totalBudget)}",
                        fontSize = if (isTablet) 14.sp else 12.sp,
                        color = AppColors.OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            color = AppColors.Primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedOverallProgress)
                            .background(
                                brush = if (summary.totalSpent > summary.totalBudget) {
                                    Brush.horizontalGradient(
                                        colors = listOf(AppColors.Error, AppColors.ExpenseLight)
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(AppColors.Primary, AppColors.Secondary)
                                    )
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category breakdown
            Text(
                text = "Category Breakdown",
                fontSize = if (isTablet) 16.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            categories.take(3).forEach { category ->
                CategoryProgressItem(category = category, isTablet = isTablet)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (categories.size > 3) {
                Text(
                    text = "and ${categories.size - 3} more categories...",
                    fontSize = if (isTablet) 12.sp else 10.sp,
                    color = AppColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CategoryProgressItem(
    category: BudgetCategory,
    isTablet: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = category.progressPercentage.coerceAtMost(1f),
        animationSpec = tween(1000, delayMillis = 200),
        label = "category_mini_progress"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isTablet) 32.dp else 28.dp)
                .background(
                    color = category.color.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(if (isTablet) 18.dp else 16.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category.name,
                    fontSize = if (isTablet) 13.sp else 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )
                Text(
                    text = "${(category.progressPercentage * 100).roundToInt()}%",
                    fontSize = if (isTablet) 13.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (category.isOverBudget) AppColors.Error else category.color
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        color = category.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(3.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            color = if (category.isOverBudget) AppColors.Error else category.color,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun BudgetAlertCard(overBudgetCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Error.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "$overBudgetCount ${if (overBudgetCount == 1) "category is" else "categories are"} over budget",
                color = AppColors.Error,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedBudgetCategoryCard(
    category: BudgetCategory,
    isTablet: Boolean,
    onBudgetChange: (Double) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val progressPercentage = category.progressPercentage.coerceAtMost(1f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = AppColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp)
        ) {
            // Header Row
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
                    Box(
                        modifier = Modifier
                            .size(if (isTablet) 52.dp else 48.dp)
                            .clip(CircleShape)
                            .background(category.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = category.color,
                            modifier = Modifier.size(if (isTablet) 24.dp else 22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = category.name,
                            fontSize = if (isTablet) 17.sp else 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.OnSurface
                        )

                        Text(
                            text = if (category.isOverBudget) "Over Budget!" else "On Track",
                            fontSize = if (isTablet) 13.sp else 11.sp,
                            color = if (category.isOverBudget) AppColors.Error else AppColors.Success,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Edit Button
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(if (isTablet) 40.dp else 36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Budget",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(if (isTablet) 22.dp else 18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Spent",
                        fontSize = if (isTablet) 13.sp else 11.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$${String.format("%.0f", category.spentAmount)}",
                        fontSize = if (isTablet) 17.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (category.isOverBudget) AppColors.Error else AppColors.OnSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Budget",
                        fontSize = if (isTablet) 13.sp else 11.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$${String.format("%.0f", category.budgetAmount)}",
                        fontSize = if (isTablet) 17.sp else 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar with Animation
            val animatedProgress by animateFloatAsState(
                targetValue = progressPercentage,
                animationSpec = tween(1200, delayMillis = 300),
                label = "category_progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        color = category.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            brush = if (category.isOverBudget) {
                                Brush.horizontalGradient(
                                    colors = listOf(AppColors.Error, AppColors.ExpenseLight)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(category.color, category.color.copy(alpha = 0.7f))
                                )
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(category.progressPercentage * 100).roundToInt()}% used",
                    fontSize = if (isTablet) 12.sp else 10.sp,
                    color = AppColors.TextSecondary
                )

                Text(
                    text = if (category.remainingAmount >= 0)
                        "$${String.format("%.0f", category.remainingAmount)} left"
                    else
                        "$${String.format("%.0f", -category.remainingAmount)} over",
                    fontSize = if (isTablet) 12.sp else 10.sp,
                    color = if (category.isOverBudget) AppColors.Error else AppColors.Success,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick Actions for Over Budget
            if (category.isOverBudget) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onBudgetChange(category.spentAmount + 100) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.Primary
                        ),
                        border = BorderStroke(1.dp, AppColors.Primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Increase Budget",
                            fontSize = if (isTablet) 12.sp else 10.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { /* Show spending details */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.TextSecondary
                        ),
                        border = BorderStroke(1.dp, AppColors.TextSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "View Details",
                            fontSize = if (isTablet) 12.sp else 10.sp
                        )
                    }
                }
            }
        }
    }

    // Edit Budget Dialog
    if (showEditDialog) {
        BudgetEditDialog(
            category = category,
            onDismiss = { showEditDialog = false },
            onSave = { newAmount ->
                onBudgetChange(newAmount)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EmptyBudgetState(
    isTablet: Boolean,
    onAddClick: () -> Unit
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
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "No budgets",
                modifier = Modifier.size(80.dp),
                tint = AppColors.TextSecondary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No budgets yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.OnSurface
            )
            Text(
                text = "Create your first budget to start tracking",
                fontSize = 14.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Budget")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, ImageVector, Color) -> Unit
) {
    var budgetName by remember { mutableStateOf("") }
    var budgetAmount by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.ShoppingCart) }
    var selectedColor by remember { mutableStateOf(AppColors.Primary) }
    var isError by remember { mutableStateOf(false) }

    val availableIcons = listOf(
        Icons.Default.Home,
        Icons.Default.Star,
        Icons.Default.ShoppingCart,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star,
        Icons.Default.Star
    )

    val availableColors = listOf(
        AppColors.Primary,
        AppColors.ExpenseLight,
        AppColors.SavingsLight,
        AppColors.Warning,
        AppColors.Success,
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF795548), // Brown
        Color(0xFF607D8B)  // Blue Grey
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Budget",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Budget Name
                OutlinedTextField(
                    value = budgetName,
                    onValueChange = { budgetName = it },
                    label = { Text("Budget Name") },
                    placeholder = { Text("e.g., Food & Dining") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Budget Amount
                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = {
                        budgetAmount = it
                        isError = it.toDoubleOrNull() == null || it.toDoubleOrNull()!! <= 0
                    },
                    label = { Text("Budget Amount") },
                    prefix = { Text("$") },
                    placeholder = { Text("0.00") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a valid amount") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Icon Selection
                Text(
                    text = "Select Icon",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableIcons) { icon ->
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { selectedIcon = icon },
                            shape = CircleShape,
                            color = if (selectedIcon == icon)
                                selectedColor.copy(alpha = 0.2f)
                            else
                                AppColors.CardBackground,
                            border = if (selectedIcon == icon)
                                BorderStroke(2.dp, selectedColor)
                            else null
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (selectedIcon == icon) selectedColor else AppColors.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection
                Text(
                    text = "Select Color",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.OnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableColors) { color ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedColor = color },
                            shape = CircleShape,
                            color = color,
                            border = if (selectedColor == color)
                                BorderStroke(3.dp, AppColors.OnSurface)
                            else
                                BorderStroke(1.dp, AppColors.TextSecondary.copy(alpha = 0.3f))
                        ) {
                            if (selectedColor == color) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    budgetAmount.toDoubleOrNull()?.let { amount ->
                        if (amount > 0 && budgetName.isNotBlank()) {
                            onSave(budgetName, amount, selectedIcon, selectedColor)
                        }
                    }
                },
                enabled = !isError && budgetName.isNotBlank() && budgetAmount.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Budget")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = AppColors.TextSecondary
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetEditDialog(
    category: BudgetCategory,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var budgetText by remember { mutableStateOf(category.budgetAmount.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = category.color.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = category.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Edit ${category.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Update your monthly budget limit:",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = budgetText,
                    onValueChange = {
                        budgetText = it
                        isError = it.toDoubleOrNull() == null || it.toDoubleOrNull()!! <= 0
                    },
                    label = { Text("Budget Amount") },
                    prefix = { Text("$") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a valid amount") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.Primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Current spending info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColors.CardBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Current Spending",
                            fontSize = 12.sp,
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${String.format("%.2f", category.spentAmount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.OnSurface
                        )

                        if (category.isOverBudget) {
                            Text(
                                text = "Currently over budget",
                                fontSize = 10.sp,
                                color = AppColors.Error,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    budgetText.toDoubleOrNull()?.let { amount ->
                        if (amount > 0) {
                            onSave(amount)
                        }
                    }
                },
                enabled = !isError && budgetText.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Update Budget")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = AppColors.TextSecondary
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}