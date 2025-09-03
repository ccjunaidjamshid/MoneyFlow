package com.example.moneyflow.presentation.ui.screens.transaction

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneyflow.data.model.*
import com.example.moneyflow.presentation.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

// App color scheme matching your existing design
object AppColors {
    val Primary = Color(0xFF2E7D32)
    val PrimaryVariant = Color(0xFF388E3C)
    val Secondary = Color(0xFF4CAF50)
    val SecondaryLight = Color(0xFF66BB6A)
    val Accent = Color(0xFFA8E6CF)
    val Background = Color(0xFFF5F8F5)
    val Surface = Color.White
    val OnSurface = Color(0xFF1B1B1B)
    val TextSecondary = Color(0xFF5F6368)
    val Error = Color(0xFFE57373)
    val Success = Color(0xFF81C784)
}

// Enhanced Category and Account Icons
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
                CategoryType.SAVINGS -> Icons.Default.MailOutline
            }
        }
    }
}

object AccountIcons {
    fun getIcon(account: Account): ImageVector {
        return when (account.accountType.toString()) {
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
fun TransactionFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringFrequency by remember { mutableStateOf<RecurringFrequency?>(null) }

    // Input validation states
    var amountError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var accountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Debug logs to check data loading
    LaunchedEffect(accounts, categories) {
        Log.d("TransactionForm", "Accounts loaded: ${accounts.size}")
        Log.d("TransactionForm", "Categories loaded: ${categories.size}")
    }

    // Auto-detect transaction type based on selected category
    LaunchedEffect(selectedCategory) {
        selectedCategory?.let { category ->
            val newType = when (category.type) {
                CategoryType.INCOME -> TransactionType.INCOME
                CategoryType.EXPENSE -> TransactionType.EXPENSE
                CategoryType.SAVINGS -> TransactionType.TRANSFER
            }
            if (newType != transactionType) {
                transactionType = newType
                Log.d("TransactionForm", "Auto-detected transaction type: $newType from category: ${category.name}")
            }
        }
    }

    // Filter categories based on transaction type
    val filteredCategories = categories.filter {
        when (transactionType) {
            TransactionType.EXPENSE -> it.type == CategoryType.EXPENSE
            TransactionType.INCOME -> it.type == CategoryType.INCOME
            TransactionType.TRANSFER -> it.type == CategoryType.SAVINGS
        }
    }

    // Input validation functions
    fun validateAmount(): Boolean {
        val amountValue = amount.toDoubleOrNull()
        amountError = when {
            amount.isBlank() -> "Amount is required"
            amountValue == null -> "Invalid amount format"
            amountValue <= 0 -> "Amount must be greater than 0"
            amountValue > 999999.99 -> "Amount cannot exceed $999,999.99"
            else -> null
        }
        return amountError == null
    }

    fun validateDescription(): Boolean {
        descriptionError = when {
            description.isBlank() -> "Description is required"
            description.length < 3 -> "Description must be at least 3 characters"
            description.length > 100 -> "Description cannot exceed 100 characters"
            else -> null
        }
        return descriptionError == null
    }

    fun validateAccount(): Boolean {
        accountError = if (selectedAccount == null) "Please select an account" else null
        return accountError == null
    }

    fun validateCategory(): Boolean {
        categoryError = if (selectedCategory == null) "Please select a category" else null
        return categoryError == null
    }

    fun validateAll(): Boolean {
        return validateAmount() && validateDescription() && validateAccount() && validateCategory()
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            viewModel.clearSuccessMessage()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = AppColors.Surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.OnSurface
                    )
                }

                Text(
                    text = "Add Transaction",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )

                Button(
                    onClick = {
                        if (validateAll()) {
                            val amountValue = amount.toDoubleOrNull()!!
                            viewModel.createTransaction(
                                accountId = selectedAccount!!.id,
                                categoryId = selectedCategory!!.id,
                                amount = amountValue,
                                type = transactionType,
                                description = description.trim(),
                                notes = if (notes.isBlank()) null else notes.trim(),
                                transactionDate = selectedDate,
                                location = null,
                                tags = null
                            )
                        }
                    },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        disabledContainerColor = AppColors.Primary.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AppColors.Surface,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Save",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Error Messages
        AnimatedVisibility(
            visible = uiState.error != null || amountError != null ||
                    descriptionError != null || accountError != null || categoryError != null
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = AppColors.Error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    uiState.error?.let { error ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = AppColors.Error,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    listOfNotNull(amountError, descriptionError, accountError, categoryError).forEach { error ->
                        if (uiState.error != null) Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = AppColors.Error,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Form Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Transaction Details Card
                TransactionDetailsCard(
                    amount = amount,
                    onAmountChange = {
                        amount = it
                        if (amountError != null) validateAmount()
                    },
                    description = description,
                    onDescriptionChange = {
                        description = it
                        if (descriptionError != null) validateDescription()
                    },
                    amountError = amountError,
                    descriptionError = descriptionError
                )
            }

            item {
                // Date and Notes Card
                DateNotesCard(
                    selectedDate = selectedDate,
                    notes = notes,
                    onNotesChange = { notes = it },
                    onDateClick = {
                        val calendar = Calendar.getInstance()
                        calendar.time = selectedDate
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newCalendar = Calendar.getInstance()
                                newCalendar.set(year, month, dayOfMonth)
                                selectedDate = newCalendar.time
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )
            }

            item {
                // Account Selection with LazyRow
                AccountSelectionCard(
                    accounts = accounts,
                    selectedAccount = selectedAccount,
                    onAccountSelected = {
                        selectedAccount = it
                        if (accountError != null) validateAccount()
                    },
                    error = accountError
                )
            }

            item {
                // Category Selection with LazyRow
                CategorySelectionCard(
                    categories = filteredCategories,
                    selectedCategory = selectedCategory,
                    transactionType = transactionType,
                    onCategorySelected = {
                        selectedCategory = it
                        if (categoryError != null) validateCategory()
                    },
                    onTransactionTypeChanged = { newType ->
                        transactionType = newType
                        selectedCategory = null
                    },
                    error = categoryError
                )
            }

            item {
                // Recurring Transaction Card
                RecurringTransactionCard(
                    isRecurring = isRecurring,
                    recurringFrequency = recurringFrequency,
                    onRecurringChange = {
                        isRecurring = it
                        if (!it) recurringFrequency = null
                        else recurringFrequency = RecurringFrequency.MONTHLY
                    },
                    onFrequencyChange = { recurringFrequency = it }
                )
            }
        }
    }
}

// Enhanced Account Selection with LazyRow
@Composable
private fun AccountSelectionCard(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit,
    error: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = if (error != null) 0.dp else 8.dp,
        color = AppColors.Surface,
        border = if (error != null) androidx.compose.foundation.BorderStroke(2.dp, AppColors.Error) else null
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (error != null) AppColors.Error else AppColors.OnSurface
                    )
                    Text(
                        text = "Choose your payment account",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (selectedAccount != null) {
                    Surface(
                        color = AppColors.Success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Success.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Success,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedAccount.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Success
                            )
                        }
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = AppColors.Error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (accounts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Star,
                    title = "No Accounts",
                    message = "Create an account first to continue"
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(accounts) { account ->
                        AccountRowItem(
                            account = account,
                            isSelected = selectedAccount?.id == account.id,
                            onClick = { onAccountSelected(account) }
                        )
                    }
                }
            }
        }
    }
}

// Enhanced Category Selection with LazyRow
@Composable
private fun CategorySelectionCard(
    categories: List<Category>,
    selectedCategory: Category?,
    transactionType: TransactionType,
    onCategorySelected: (Category) -> Unit,
    onTransactionTypeChanged: (TransactionType) -> Unit,
    error: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = if (error != null) 0.dp else 8.dp,
        color = AppColors.Surface,
        border = if (error != null) androidx.compose.foundation.BorderStroke(2.dp, AppColors.Error) else null
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Category",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (error != null) AppColors.Error else AppColors.OnSurface
                    )
                    Text(
                        text = "Choose transaction category",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (selectedCategory != null) {
                    Surface(
                        color = AppColors.Success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Success.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Success,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCategory.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Success
                            )
                        }
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = AppColors.Error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Transaction Type Toggle
            Surface(
                color = AppColors.Background,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionType.values().forEach { type ->
                        val isSelected = transactionType == type
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            animationSpec = tween(200)
                        )

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .scale(scale)
                                .clickable { onTransactionTypeChanged(type) },
                            color = if (isSelected) {
                                when (type) {
                                    TransactionType.EXPENSE -> AppColors.Error.copy(alpha = 0.15f)
                                    TransactionType.INCOME -> AppColors.Success.copy(alpha = 0.15f)
                                    TransactionType.TRANSFER -> AppColors.Primary.copy(alpha = 0.15f)
                                }
                            } else AppColors.Surface,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = if (isSelected) 6.dp else 2.dp,
                            border = if (isSelected) {
                                androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    when (type) {
                                        TransactionType.EXPENSE -> AppColors.Error
                                        TransactionType.INCOME -> AppColors.Success
                                        TransactionType.TRANSFER -> AppColors.Primary
                                    }
                                )
                            } else null
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    when (type) {
                                        TransactionType.EXPENSE -> Icons.Default.ArrowDropDown
                                        TransactionType.INCOME -> Icons.Default.ArrowForward
                                        TransactionType.TRANSFER -> Icons.Default.Send
                                    },
                                    contentDescription = null,
                                    tint = when (type) {
                                        TransactionType.EXPENSE -> AppColors.Error
                                        TransactionType.INCOME -> AppColors.Success
                                        TransactionType.TRANSFER -> AppColors.Primary
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = when (type) {
                                        TransactionType.EXPENSE -> AppColors.Error
                                        TransactionType.INCOME -> AppColors.Success
                                        TransactionType.TRANSFER -> AppColors.Primary
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (categories.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Edit,
                    title = "No Categories",
                    message = "Create ${transactionType.name.lowercase()} categories first"
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(categories) { category ->
                        CategoryRowItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }
        }
    }
}

// Account Row Item Component
@Composable
private fun AccountRowItem(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(300)
    )

    Surface(
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .width(140.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 12.dp else 6.dp,
        color = if (isSelected) AppColors.Primary.copy(alpha = 0.1f) else AppColors.Surface,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, AppColors.Primary)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.1f),
                    shadowElevation = if (isSelected) 8.dp else 4.dp
                ) {
                    Icon(
                        AccountIcons.getIcon(account),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        tint = if (isSelected) AppColors.Surface else AppColors.Primary
                    )
                }

                if (isSelected) {
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 20.dp, y = (-20).dp),
                        shape = CircleShape,
                        color = AppColors.Success,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = AppColors.Surface,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = account.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) AppColors.Primary else AppColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$${String.format("%.2f", account.balance)}",
                fontSize = 12.sp,
                color = if (account.balance >= 0) AppColors.Success else AppColors.Error,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = account.accountType.name.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 11.sp,
                color = AppColors.TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Category Row Item Component
@Composable
private fun CategoryRowItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(300)
    )

    val categoryColor = when (category.type) {
        CategoryType.INCOME -> AppColors.Success
        CategoryType.EXPENSE -> AppColors.Error
        CategoryType.SAVINGS -> AppColors.Primary
    }

    Surface(
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .width(120.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 12.dp else 6.dp,
        color = if (isSelected) categoryColor.copy(alpha = 0.1f) else AppColors.Surface,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, categoryColor)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = if (isSelected) categoryColor else categoryColor.copy(alpha = 0.1f),
                    shadowElevation = if (isSelected) 8.dp else 4.dp
                ) {
                    Icon(
                        CategoryIcons.getIcon(category),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        tint = if (isSelected) AppColors.Surface else categoryColor
                    )
                }

                if (isSelected) {
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 20.dp, y = (-20).dp),
                        shape = CircleShape,
                        color = AppColors.Success,
                        shadowElevation = 4.dp
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = AppColors.Surface,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) categoryColor else AppColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = categoryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = category.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    color = categoryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// Empty state component
@Composable
private fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = AppColors.Background,
                shadowElevation = 2.dp
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TransactionDetailsCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    amountError: String?,
    descriptionError: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        color = AppColors.Surface
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Transaction Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Enter amount and description",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (amountError == null && descriptionError == null &&
                    amount.isNotEmpty() && description.isNotEmpty()) {
                    Surface(
                        color = AppColors.Success.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Success.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Success,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Valid",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Success
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    // Only allow numbers and one decimal point
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        onAmountChange(newValue)
                    }
                },
                label = { Text("Amount *", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Surface(
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.MailOutline,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                trailingIcon = {
                    if (amountError == null && amount.isNotEmpty()) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Valid",
                            tint = AppColors.Success,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                isError = amountError != null,
                supportingText = if (amountError != null) {
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                amountError,
                                color = AppColors.Error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (amountError != null) AppColors.Error else AppColors.Primary,
                    unfocusedBorderColor = if (amountError != null) AppColors.Error else Color.Gray.copy(alpha = 0.3f),
                    focusedLabelColor = AppColors.Primary,
                    cursorColor = AppColors.Primary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description *", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Surface(
                        color = AppColors.Secondary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = AppColors.Secondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (description.length > 100)
                                AppColors.Error.copy(alpha = 0.1f)
                            else
                                AppColors.TextSecondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${description.length}/100",
                                fontSize = 11.sp,
                                color = if (description.length > 100) AppColors.Error else AppColors.TextSecondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        if (descriptionError == null && description.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Valid",
                                tint = AppColors.Success,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                isError = descriptionError != null,
                supportingText = if (descriptionError != null) {
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                descriptionError,
                                color = AppColors.Error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (descriptionError != null) AppColors.Error else AppColors.Primary,
                    unfocusedBorderColor = if (descriptionError != null) AppColors.Error else Color.Gray.copy(alpha = 0.3f),
                    focusedLabelColor = AppColors.Primary,
                    cursorColor = AppColors.Primary
                )
            )
        }
    }
}

@Composable
private fun DateNotesCard(
    selectedDate: Date,
    notes: String,
    onNotesChange: (String) -> Unit,
    onDateClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate) ==
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        color = AppColors.Surface
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Date & Notes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "When did this transaction occur?",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isToday) {
                    Surface(
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = AppColors.Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Today",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = dateFormat.format(selectedDate),
                onValueChange = { },
                label = { Text("Transaction Date", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Surface(
                        color = AppColors.Accent.copy(alpha = 0.3f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            tint = AppColors.Primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                trailingIcon = {
                    IconButton(onClick = onDateClick) {
                        Surface(
                            color = AppColors.Primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Select date",
                                tint = AppColors.Primary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateClick() },
                enabled = false,
                readOnly = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = AppColors.Primary.copy(alpha = 0.5f),
                    disabledTextColor = AppColors.OnSurface,
                    disabledLabelColor = AppColors.Primary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { newValue ->
                    if (newValue.length <= 200) {
                        onNotesChange(newValue)
                    }
                },
                label = { Text("Notes (Optional)", fontWeight = FontWeight.SemiBold) },
                leadingIcon = {
                    Surface(
                        color = AppColors.SecondaryLight.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = AppColors.SecondaryLight,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                trailingIcon = {
                    Surface(
                        color = if (notes.length > 180)
                            AppColors.Error.copy(alpha = 0.1f)
                        else
                            AppColors.TextSecondary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${notes.length}/200",
                            fontSize = 11.sp,
                            color = if (notes.length > 180) AppColors.Error else AppColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                placeholder = {
                    Text(
                        "Add any additional notes...",
                        color = AppColors.TextSecondary.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    focusedLabelColor = AppColors.Primary,
                    cursorColor = AppColors.Primary
                )
            )
        }
    }
}

@Composable
private fun RecurringTransactionCard(
    isRecurring: Boolean,
    recurringFrequency: RecurringFrequency?,
    onRecurringChange: (Boolean) -> Unit,
    onFrequencyChange: (RecurringFrequency) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        color = AppColors.Surface
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recurring Transaction",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Set up automatic recurring transactions",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isRecurring) AppColors.Primary.copy(alpha = 0.1f) else AppColors.Background
                ) {
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = onRecurringChange,
                        modifier = Modifier.padding(4.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppColors.Primary,
                            checkedTrackColor = AppColors.Primary.copy(alpha = 0.3f),
                            uncheckedThumbColor = AppColors.TextSecondary,
                            uncheckedTrackColor = AppColors.TextSecondary.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            AnimatedVisibility(visible = isRecurring) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        color = AppColors.Background,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = AppColors.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Frequency",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.OnSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(RecurringFrequency.values().toList()) { frequency ->
                                    val isSelected = recurringFrequency == frequency

                                    FilterChip(
                                        onClick = { onFrequencyChange(frequency) },
                                        label = {
                                            Text(
                                                frequency.name.lowercase().replaceFirstChar { it.uppercase() },
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                            )
                                        },
                                        selected = isSelected,
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AppColors.Primary,
                                            selectedLabelColor = AppColors.Surface,
                                            selectedLeadingIconColor = AppColors.Surface,
                                            containerColor = AppColors.Surface,
                                            labelColor = AppColors.OnSurface
                                        ),
                                        border = if (!isSelected) {
                                            androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                AppColors.Primary.copy(alpha = 0.3f)
                                            )
                                        } else null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}