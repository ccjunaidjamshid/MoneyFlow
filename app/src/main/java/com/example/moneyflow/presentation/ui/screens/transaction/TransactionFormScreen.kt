package com.example.moneyflow.presentation.ui.screens.transaction

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

// Simple, clean color scheme
object AppColors {
    val Primary = Color(0xFF2E7D32)
    val Secondary = Color(0xFF4CAF50)
    val Background = Color(0xFFFAFAFA)
    val Surface = Color.White
    val OnSurface = Color(0xFF1A1A1A)
    val TextSecondary = Color(0xFF666666)
    val Error = Color(0xFFD32F2F)
    val Success = Color(0xFF388E3C)
    val Divider = Color(0xFFE0E0E0)
}

// Keep existing icon mapping
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

    // Validation states
    var amountError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var accountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val categories by viewModel.categories.collectAsState()

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

    // Validation functions
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
        // Clean header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppColors.Surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.OnSurface
                    )
                }

                Text(
                    text = "Add Transaction",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save", fontSize = 14.sp)
                    }
                }
            }
        }

        // Error messages
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppColors.Error.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = AppColors.Error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = error,
                        color = AppColors.Error,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Form content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount and description
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Transaction Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    amount = newValue
                                    if (amountError != null) validateAmount()
                                }
                            },
                            label = { Text("Amount") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.MailOutline,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            isError = amountError != null,
                            supportingText = amountError?.let { { Text(it, color = AppColors.Error) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = {
                                description = it
                                if (descriptionError != null) validateDescription()
                            },
                            label = { Text("Description") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            isError = descriptionError != null,
                            supportingText = descriptionError?.let { { Text(it, color = AppColors.Error) } },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Date and notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Date & Notes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

                        OutlinedTextField(
                            value = dateFormat.format(selectedDate),
                            onValueChange = { },
                            label = { Text("Date") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
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
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Select date",
                                        tint = AppColors.Primary
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            readOnly = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { if (it.length <= 200) notes = it },
                            label = { Text("Notes (Optional)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = AppColors.Primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            }

            // Transaction type and category
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface
                        )

                        if (categoryError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = categoryError!!,
                                color = AppColors.Error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transaction type buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TransactionType.values().forEach { type ->
                                val isSelected = transactionType == type

                                FilterChip(
                                    onClick = {
                                        transactionType = type
                                        selectedCategory = null
                                    },
                                    label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    selected = isSelected,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (filteredCategories.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "No categories available",
                                        color = AppColors.TextSecondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            // Enhanced grid-like layout for categories
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(filteredCategories) { category ->
                                    CategoryItem(
                                        category = category,
                                        isSelected = selectedCategory?.id == category.id,
                                        onClick = {
                                            selectedCategory = category
                                            if (categoryError != null) validateCategory()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Account selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.OnSurface
                        )

                        if (accountError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = accountError!!,
                                color = AppColors.Error,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (accounts.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AppColors.TextSecondary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "No accounts available",
                                        color = AppColors.TextSecondary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            // Enhanced grid-like layout for accounts
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                items(accounts) { account ->
                                    AccountItem(
                                        account = account,
                                        isSelected = selectedAccount?.id == account.id,
                                        onClick = {
                                            selectedAccount = account
                                            if (accountError != null) validateAccount()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recurring transaction
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recurring Transaction",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.OnSurface
                            )

                            Switch(
                                checked = isRecurring,
                                onCheckedChange = {
                                    isRecurring = it
                                    if (!it) {
                                        recurringFrequency = null
                                    } else {
                                        recurringFrequency = RecurringFrequency.MONTHLY
                                    }
                                }
                            )
                        }

                        AnimatedVisibility(visible = isRecurring) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))

                                Divider(color = AppColors.Divider)

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Frequency",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.OnSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(RecurringFrequency.values().toList()) { frequency ->
                                        FilterChip(
                                            onClick = { recurringFrequency = frequency },
                                            label = {
                                                Text(frequency.name.lowercase().replaceFirstChar { it.uppercase() })
                                            },
                                            selected = recurringFrequency == frequency
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
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = when (category.type) {
        CategoryType.INCOME -> AppColors.Success
        CategoryType.EXPENSE -> AppColors.Error
        CategoryType.SAVINGS -> AppColors.Primary
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200)
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular category indicator with clean borders
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) 
                        categoryColor 
                    else 
                        AppColors.Surface
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) categoryColor else categoryColor.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                CategoryIcons.getIcon(category),
                contentDescription = null,
                tint = if (isSelected) Color.White else categoryColor,
                modifier = Modifier.size(28.dp)
            )
            
            // Clean selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, categoryColor, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = categoryColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Category name with better typography
        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) categoryColor else AppColors.OnSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(72.dp)
        )
    }
}

@Composable
private fun AccountItem(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200)
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular account indicator with clean borders
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) 
                        AppColors.Primary 
                    else 
                        AppColors.Surface
                )
                .border(
                    width = 2.dp,
                    color = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.4f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                AccountIcons.getIcon(account),
                contentDescription = null,
                tint = if (isSelected) Color.White else AppColors.Primary,
                modifier = Modifier.size(28.dp)
            )
            
            // Clean selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, AppColors.Primary, CircleShape)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = AppColors.Primary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Account name with better typography
        Text(
            text = account.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) AppColors.Primary else AppColors.OnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(72.dp)
        )
        
        // Balance with subtle styling
        Text(
            text = "$${String.format("%.0f", account.balance)}",
            fontSize = 10.sp,
            color = if (account.balance >= 0) AppColors.Success.copy(alpha = 0.8f) else AppColors.Error.copy(alpha = 0.8f),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}