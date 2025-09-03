package com.example.moneyflow.presentation.ui.screens.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.presentation.viewmodel.CategoryViewModel
import com.example.moneyflow.presentation.ui.theme.AppColors
import com.example.moneyflow.utils.CategoryColors
import com.example.moneyflow.utils.CategoryIcons

/**
 * Main Category Screen with category management functionality
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    val hapticFeedback = LocalHapticFeedback.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categorySummary by viewModel.categorySummary.collectAsStateWithLifecycle()
    val selectedCategoryType by viewModel.selectedCategoryType.collectAsStateWithLifecycle()
    
    // Dialog states
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()
    val showEditDialog by viewModel.showEditDialog.collectAsStateWithLifecycle()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    // Animation state
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Show success message
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header with Create Button - Same style as AccountScreen
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
                        text = "Categories",
                        fontSize = if (isTablet) 26.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    if (categorySummary != null) {
                        Text(
                            text = "Total: ${categorySummary!!.totalCategories} categories",
                            fontSize = if (isTablet) 16.sp else 14.sp,
                            color = AppColors.Secondary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${categorySummary!!.incomeCategories} Income • ${categorySummary!!.expenseCategories} Expense • ${categorySummary!!.savingsCategories} Savings",
                            fontSize = if (isTablet) 13.sp else 11.sp,
                            color = AppColors.TextSecondary
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { viewModel.showCreateDialog() },
                    modifier = Modifier.size(if (isTablet) 56.dp else 48.dp),
                    containerColor = AppColors.Primary,
                    contentColor = AppColors.Surface,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Category",
                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                    )
                }
            }
        }

        // Success Message
        if (uiState.successMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = AppColors.Success.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = uiState.successMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = AppColors.Success,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Filter chips - Same style as AccountScreen
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
            color = AppColors.Surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { viewModel.filterByType(null) },
                    label = { 
                        Text(
                            "All",
                            fontSize = if (isTablet) 14.sp else 12.sp,
                            fontWeight = FontWeight.Medium
                        ) 
                    },
                    selected = selectedCategoryType == null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.Primary,
                        selectedLabelColor = AppColors.Surface
                    )
                )
                
                CategoryType.values().forEach { type ->
                    FilterChip(
                        onClick = { viewModel.filterByType(type) },
                        label = { 
                            Text(
                                type.displayName,
                                fontSize = if (isTablet) 14.sp else 12.sp,
                                fontWeight = FontWeight.Medium
                            ) 
                        },
                        selected = selectedCategoryType == type,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.Primary,
                            selectedLabelColor = AppColors.Surface
                        )
                    )
                }
            }
        }

        // Categories list or empty state
        if (categories.isEmpty()) {
            // Empty State - Same style as AccountScreen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = com.example.moneyflow.R.drawable.category),
                        contentDescription = "No categories",
                        modifier = Modifier.size(80.dp),
                        tint = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No categories yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Create your first category to get started",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.showCreateDialog() },
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
                        Text("Create Category")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 2 }
                        )
                    ) {
                        CategoryItem(
                            category = category,
                            isTablet = isTablet,
                            onLongClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Show menu options
                            },
                            onEdit = { viewModel.showEditDialog(category) },
                            onDelete = { viewModel.showDeleteDialog(category) }
                        )
                    }
                }
            }
        }
    }
    
    // Show dialogs
    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onConfirm = { name, type, iconName, color ->
                viewModel.createCategory(name, type, iconName, color)
            }
        )
    }
    
    if (showEditDialog && selectedCategory != null) {
        EditCategoryDialog(
            category = selectedCategory!!,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { category: Category ->
                viewModel.updateCategory(category)
            }
        )
    }
    
    if (showDeleteDialog && selectedCategory != null) {
        DeleteCategoryDialog(
            category = selectedCategory!!,
            onDismiss = { viewModel.hideDeleteDialog() },
            onConfirm = {
                viewModel.deleteCategory(selectedCategory!!.id)
            }
        )
    }
}

/**
 * Individual category item with actions - Same style as AccountItem
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryItem(
    category: Category,
    isTablet: Boolean,
    onLongClick: () -> Unit = {},
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val categoryColor = CategoryColors.hexToColor(category.color)
    val iconRes = CategoryIcons.getIconByName(category.iconName) ?: com.example.moneyflow.R.drawable.category

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { /* Handle category selection if needed */ },
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        color = AppColors.Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon - Same style as AccountItem
            Box(
                modifier = Modifier
                    .size(if (isTablet) 52.dp else 48.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = category.name,
                    modifier = Modifier.size(if (isTablet) 24.dp else 22.dp),
                    tint = categoryColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Category Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = if (isTablet) 17.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface
                )

                Text(
                    text = category.type.displayName,
                    fontSize = if (isTablet) 13.sp else 11.sp,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (category.isDefault) {
                    Surface(
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Default Category",
                            fontSize = if (isTablet) 10.sp else 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Menu button
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(if (isTablet) 32.dp else 28.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert, 
                        contentDescription = "More options",
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(if (isTablet) 20.dp else 18.dp)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = AppColors.OnSurface) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    
                    if (!category.isDefault) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = AppColors.Error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}