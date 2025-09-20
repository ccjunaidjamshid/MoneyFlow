package com.example.moneyflow.presentation.ui.screens.transaction

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyflow.data.model.*
import com.example.moneyflow.presentation.viewmodel.TransactionViewModel
import com.example.moneyflow.presentation.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.*

// Icon mapping (unchanged)
object CategoryIcons {
    fun getIcon(category: Category): ImageVector {
        return when (category.name.lowercase()) {
            "food", "groceries", "dining", "restaurant" -> Icons.Default.Star
            "transport", "gas", "car", "fuel" -> Icons.Default.Star
            "shopping", "clothes", "fashion" -> Icons.Default.Star
            "entertainment", "movies", "fun" -> Icons.Default.Star
            "health", "medical", "doctor" -> Icons.Default.Star
            "utilities", "bills", "electricity" -> Icons.Default.Home
            "education", "books", "learning" -> Icons.Default.Star
            "salary", "income", "work" -> Icons.Default.Star
            "gifts", "bonus" -> Icons.Default.Star
            "savings", "investment" -> Icons.Default.Star
            "travel", "vacation", "trip" -> Icons.Default.Star
            "gym", "fitness", "sports" -> Icons.Default.Star
            "phone", "internet", "subscription" -> Icons.Default.Phone
            else -> when (category.type) {
                CategoryType.INCOME -> Icons.Default.KeyboardArrowUp
                CategoryType.EXPENSE -> Icons.Default.KeyboardArrowDown
                CategoryType.SAVINGS -> Icons.Default.Star
            }
        }
    }
}

object AccountIcons {
    fun getIcon(account: Account): ImageVector {
        return when (account.accountType.toString().lowercase()) {
            "checking", "current" -> Icons.Default.Star
            "savings" -> Icons.Default.Star
            "credit" -> Icons.Default.Star
            "cash" -> Icons.Default.Star
            "investment" -> Icons.Default.Star
            else -> Icons.Default.Star
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val isTablet = configuration.screenWidthDp >= 600

    // Calculate responsive dimensions
    val dialogWidth = when {
        isTablet -> 0.7f
        isLandscape -> 0.9f
        else -> 0.95f
    }

    val dialogMaxHeight = when {
        isLandscape -> configuration.screenHeightDp.dp - 32.dp
        else -> configuration.screenHeightDp.dp * 0.85f
    }

    // Form state
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Get data
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    // Filter categories by transaction type
    val filteredCategories = remember(categories, transactionType) {
        categories.filter { category ->
            when (transactionType) {
                TransactionType.INCOME -> category.type == CategoryType.INCOME
                TransactionType.EXPENSE -> category.type == CategoryType.EXPENSE
                TransactionType.TRANSFER -> false
            }
        }
    }

    // Validation
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var accountError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        var isValid = true

        if (amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            amountError = "Please enter a valid amount"
            isValid = false
        } else {
            amountError = null
        }

        if (selectedCategory == null && transactionType != TransactionType.TRANSFER) {
            categoryError = "Please select a category"
            isValid = false
        } else {
            categoryError = null
        }

        if (selectedAccount == null) {
            accountError = "Please select an account"
            isValid = false
        } else {
            accountError = null
        }

        return isValid
    }

    // Handle success
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            viewModel.clearSuccessMessage()
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(dialogWidth)
                    .heightIn(max = dialogMaxHeight)
                    .padding(if (isTablet) 24.dp else 16.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Enhanced Header with gradient
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            AppColors.Primary,
                                            AppColors.Primary.copy(alpha = 0.8f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                )
                                .padding(
                                    horizontal = if (isTablet) 32.dp else 24.dp,
                                    vertical = if (isTablet) 28.dp else 24.dp
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Add Transaction",
                                        fontSize = if (isTablet) 24.sp else 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Track your financial activity",
                                        fontSize = if (isTablet) 16.sp else 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size(if (isTablet) 48.dp else 40.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.White,
                                        modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Scrollable Content
                    if (isLandscape && !isTablet) {
                        // Horizontal layout for landscape phones
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(20.dp)
                        ) {
                            // Left column
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ErrorSection(uiState.error)
                                TransactionTypeSection(transactionType) { newType ->
                                    transactionType = newType
                                    selectedCategory = null
                                }
                                if (transactionType != TransactionType.TRANSFER) {
                                    CategoriesSection(
                                        filteredCategories = filteredCategories,
                                        selectedCategory = selectedCategory,
                                        transactionType = transactionType,
                                        categoryError = categoryError,
                                        onCategorySelect = { selectedCategory = it },
                                        isTablet = false,
                                        useGrid = true
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Right column
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AccountsSection(
                                    accounts = accounts,
                                    selectedAccount = selectedAccount,
                                    accountError = accountError,
                                    onAccountSelect = { selectedAccount = it },
                                    isTablet = false,
                                    useGrid = true
                                )
                                FormFieldsSection(
                                    amount = amount,
                                    onAmountChange = { amount = it },
                                    amountError = amountError,
                                    description = description,
                                    onDescriptionChange = { description = it },
                                    selectedDate = selectedDate,
                                    dateFormatter = dateFormatter,
                                    onDateClick = { showDatePicker = true },
                                    isTablet = false
                                )
                                SubmitButtonSection(
                                    isLoading = uiState.isLoading,
                                    onSubmit = {
                                        if (validateForm()) {
                                            viewModel.createTransaction(
                                                accountId = selectedAccount?.id ?: 0L,
                                                categoryId = if (transactionType != TransactionType.TRANSFER) selectedCategory?.id ?: 0L else 0L,
                                                amount = amount.toDouble(),
                                                type = transactionType,
                                                description = description.ifBlank { null },
                                                notes = null,
                                                transactionDate = selectedDate,
                                                location = null,
                                                tags = null
                                            )
                                        }
                                    },
                                    isTablet = false
                                )
                            }
                        }
                    } else {
                        // Vertical layout for portrait and tablets
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(if (isTablet) 32.dp else 20.dp),
                            verticalArrangement = Arrangement.spacedBy(if (isTablet) 24.dp else 16.dp)
                        ) {
                            item { ErrorSection(uiState.error) }

                            item {
                                TransactionTypeSection(transactionType) { newType ->
                                    transactionType = newType
                                    selectedCategory = null
                                }
                            }

                            if (transactionType != TransactionType.TRANSFER) {
                                item {
                                    CategoriesSection(
                                        filteredCategories = filteredCategories,
                                        selectedCategory = selectedCategory,
                                        transactionType = transactionType,
                                        categoryError = categoryError,
                                        onCategorySelect = { selectedCategory = it },
                                        isTablet = isTablet,
                                        useGrid = isTablet
                                    )
                                }
                            }

                            item {
                                AccountsSection(
                                    accounts = accounts,
                                    selectedAccount = selectedAccount,
                                    accountError = accountError,
                                    onAccountSelect = { selectedAccount = it },
                                    isTablet = isTablet,
                                    useGrid = isTablet
                                )
                            }

                            item {
                                FormFieldsSection(
                                    amount = amount,
                                    onAmountChange = { amount = it },
                                    amountError = amountError,
                                    description = description,
                                    onDescriptionChange = { description = it },
                                    selectedDate = selectedDate,
                                    dateFormatter = dateFormatter,
                                    onDateClick = { showDatePicker = true },
                                    isTablet = isTablet
                                )
                            }

                            item {
                                SubmitButtonSection(
                                    isLoading = uiState.isLoading,
                                    onSubmit = {
                                        if (validateForm()) {
                                            viewModel.createTransaction(
                                                accountId = selectedAccount?.id ?: 0L,
                                                categoryId = if (transactionType != TransactionType.TRANSFER) selectedCategory?.id ?: 0L else 0L,
                                                amount = amount.toDouble(),
                                                type = transactionType,
                                                description = description.ifBlank { null },
                                                notes = null,
                                                transactionDate = selectedDate,
                                                location = null,
                                                tags = null
                                            )
                                        }
                                    },
                                    isTablet = isTablet
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            show()
        }
    }
}

@Composable
private fun ErrorSection(error: String?) {
    AnimatedVisibility(
        visible = error != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        error?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Error.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = it,
                        color = AppColors.Error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionTypeSection(
    transactionType: TransactionType,
    onTypeChange: (TransactionType) -> Unit
) {
    Column {
        Text(
            text = "Transaction Type",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionType.values().forEach { type ->
                val isSelected = transactionType == type
                FilterChip(
                    onClick = { onTypeChange(type) },
                    label = {
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    selected = isSelected,
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppColors.Primary.copy(alpha = 0.2f),
                        selectedLabelColor = AppColors.Primary
                    ),
                    border = if (isSelected) {
                        FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = AppColors.Primary,
                            selectedBorderColor = AppColors.Primary
                        )
                    } else {
                        FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = AppColors.OnSurface.copy(alpha = 0.3f)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoriesSection(
    filteredCategories: List<Category>,
    selectedCategory: Category?,
    transactionType: TransactionType,
    categoryError: String?,
    onCategorySelect: (Category) -> Unit,
    isTablet: Boolean,
    useGrid: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )

            if (filteredCategories.isNotEmpty()) {
                Text(
                    text = "${filteredCategories.size} available",
                    fontSize = if (isTablet) 14.sp else 12.sp,
                    color = AppColors.OnSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredCategories.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Default.Warning,
                title = "No categories available for ${transactionType.name.lowercase()}",
                subtitle = "Please create a category first",
                isTablet = isTablet
            )
        } else {
            if (useGrid) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(if (isTablet) 4 else 3),
                    modifier = Modifier.heightIn(max = if (isTablet) 300.dp else 200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(filteredCategories) { category ->
                        CategoryItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { onCategorySelect(category) },
                            isTablet = isTablet
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(filteredCategories) { category ->
                        CategoryItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { onCategorySelect(category) },
                            isTablet = isTablet
                        )
                    }
                }
            }
        }

        categoryError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = AppColors.Error,
                fontSize = if (isTablet) 14.sp else 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AccountsSection(
    accounts: List<Account>,
    selectedAccount: Account?,
    accountError: String?,
    onAccountSelect: (Account) -> Unit,
    isTablet: Boolean,
    useGrid: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Accounts",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )

            if (accounts.isNotEmpty()) {
                Text(
                    text = "${accounts.size} available",
                    fontSize = if (isTablet) 14.sp else 12.sp,
                    color = AppColors.OnSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (accounts.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Default.AccountBox,
                title = "No accounts available",
                subtitle = "Please create an account first",
                isTablet = isTablet
            )
        } else {
            if (useGrid) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(if (isTablet) 4 else 3),
                    modifier = Modifier.heightIn(max = if (isTablet) 300.dp else 200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(accounts) { account ->
                        AccountItem(
                            account = account,
                            isSelected = selectedAccount?.id == account.id,
                            onClick = { onAccountSelect(account) },
                            isTablet = isTablet
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(accounts) { account ->
                        AccountItem(
                            account = account,
                            isSelected = selectedAccount?.id == account.id,
                            onClick = { onAccountSelect(account) },
                            isTablet = isTablet
                        )
                    }
                }
            }
        }

        accountError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = AppColors.Error,
                fontSize = if (isTablet) 14.sp else 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FormFieldsSection(
    amount: String,
    onAmountChange: (String) -> Unit,
    amountError: String?,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDate: Date,
    dateFormatter: SimpleDateFormat,
    onDateClick: () -> Unit,
    isTablet: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(if (isTablet) 20.dp else 16.dp)) {
        // Amount field
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Amount *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = amountError != null,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (amountError != null) AppColors.Error else AppColors.Primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                focusedLabelColor = AppColors.Primary,
                cursorColor = AppColors.Primary,
                errorBorderColor = AppColors.Error,
                errorLabelColor = AppColors.Error
            ),
            shape = RoundedCornerShape(16.dp)
        )

        amountError?.let {
            Text(
                text = it,
                color = AppColors.Error,
                fontSize = if (isTablet) 14.sp else 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                focusedLabelColor = AppColors.Primary,
                cursorColor = AppColors.Primary
            ),
            shape = RoundedCornerShape(16.dp)
        )

        // Date field
        OutlinedTextField(
            value = dateFormatter.format(selectedDate),
            onValueChange = { },
            label = { Text("Date") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            enabled = false,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AppColors.Primary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date",
                    tint = AppColors.Primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = AppColors.Primary.copy(alpha = 0.5f),
                disabledLabelColor = AppColors.Primary.copy(alpha = 0.7f),
                disabledTextColor = AppColors.OnSurface
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SubmitButtonSection(
    isLoading: Boolean,
    onSubmit: () -> Unit,
    isTablet: Boolean
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isTablet) 64.dp else 56.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            contentColor = Color.White,
            disabledContainerColor = AppColors.Primary.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(if (isTablet) 24.dp else 20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Adding Transaction...",
                fontSize = if (isTablet) 16.sp else 14.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(if (isTablet) 24.dp else 20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Add Transaction",
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isTablet: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Error.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isTablet) 32.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(if (isTablet) 72.dp else 64.dp)
                    .background(
                        AppColors.Error.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.Error,
                    modifier = Modifier.size(if (isTablet) 36.dp else 32.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (isTablet) 20.dp else 16.dp))

            Text(
                text = title,
                color = AppColors.Error,
                fontSize = if (isTablet) 18.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                color = AppColors.OnSurface.copy(alpha = 0.7f),
                fontSize = if (isTablet) 16.sp else 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300),
        label = "category_scale"
    )

    val itemSize = if (isTablet) 80.dp else 72.dp
    val iconSize = if (isTablet) 28.dp else 24.dp
    val borderWidth = if (isSelected) 3.dp else 1.5.dp
    val borderColor = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(itemSize)
                .shadow(
                    elevation = if (isSelected) 8.dp else 2.dp,
                    shape = CircleShape,
                    ambientColor = AppColors.Primary.copy(alpha = 0.1f),
                    spotColor = AppColors.Primary.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    brush = if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.15f),
                                AppColors.Primary.copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                )
                .padding(if (isTablet) 20.dp else 16.dp)
        ) {
            Icon(
                imageVector = CategoryIcons.getIcon(category),
                contentDescription = category.name,
                tint = if (isSelected) AppColors.Primary else AppColors.OnSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(iconSize)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = AppColors.Primary,
                    modifier = Modifier
                        .size(if (isTablet) 20.dp else 16.dp)
                        .offset(x = (itemSize / 3), y = -(itemSize / 3))
                        .background(Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isTablet) 12.dp else 8.dp))

        Text(
            text = category.name,
            fontSize = if (isTablet) 14.sp else 12.sp,
            color = if (isSelected) AppColors.Primary else AppColors.OnSurface.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(if (isTablet) 100.dp else 90.dp)
        )
    }
}

@Composable
private fun AccountItem(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(300),
        label = "account_scale"
    )

    val itemSize = if (isTablet) 80.dp else 72.dp
    val iconSize = if (isTablet) 28.dp else 24.dp
    val borderWidth = if (isSelected) 3.dp else 1.5.dp
    val borderColor = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(itemSize)
                .shadow(
                    elevation = if (isSelected) 8.dp else 2.dp,
                    shape = CircleShape,
                    ambientColor = AppColors.Primary.copy(alpha = 0.1f),
                    spotColor = AppColors.Primary.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .background(
                    brush = if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                AppColors.Primary.copy(alpha = 0.15f),
                                AppColors.Primary.copy(alpha = 0.05f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                )
                .padding(if (isTablet) 20.dp else 16.dp)
        ) {
            Icon(
                imageVector = AccountIcons.getIcon(account),
                contentDescription = account.name,
                tint = if (isSelected) AppColors.Primary else AppColors.OnSurface.copy(alpha = 0.7f),
                modifier = Modifier.size(iconSize)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = AppColors.Primary,
                    modifier = Modifier
                        .size(if (isTablet) 20.dp else 16.dp)
                        .offset(x = (itemSize / 3), y = -(itemSize / 3))
                        .background(Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isTablet) 12.dp else 8.dp))

        Text(
            text = account.name,
            fontSize = if (isTablet) 14.sp else 12.sp,
            color = if (isSelected) AppColors.Primary else AppColors.OnSurface.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(if (isTablet) 100.dp else 90.dp)
        )

        Text(
            text = "${String.format("%.2f", account.balance)}",
            fontSize = if (isTablet) 12.sp else 10.sp,
            color = if (account.balance >= 0) {
                AppColors.Success.copy(alpha = 0.8f)
            } else {
                AppColors.Error.copy(alpha = 0.8f)
            },
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    color = if (account.balance >= 0) {
                        AppColors.Success.copy(alpha = 0.1f)
                    } else {
                        AppColors.Error.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

// Backward compatibility wrapper
@Composable
fun TransactionFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        TransactionFormDialog(
            onDismiss = {
                showDialog = false
                onNavigateBack()
            },
            viewModel = viewModel
        )
    } else {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
}