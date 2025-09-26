package com.example.moneyflow.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneyflow.data.model.*

// Color constants
private val primaryGreen = Color(0xFF10B981)
private val lightGreen = Color(0xFFECFDF5)
private val expenseRed = Color(0xFFEF4444)
private val lightRed = Color(0xFFFEF2F2)
private val transferBlue = Color(0xFF3B82F6)
private val lightBlue = Color(0xFFEFF6FF)
private val surfaceColor = Color(0xFFFAFAFA)
private val cardColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onAdd: (Double, TransactionType, Long, Long, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf<Account?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf("") }
    var showAccountPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    val mockAccounts = remember { getMockAccounts() }
    val mockCategories = remember { getMockCategories() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color.Black.copy(alpha = 0.08f)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(cardColor, surfaceColor.copy(alpha = 0.3f))
                        )
                    )
            ) {
                DialogHeader(onDismiss = onDismiss)

                Column(modifier = Modifier.padding(24.dp)) {
                    TransactionTypeSelector(
                        selectedType = selectedType,
                        onTypeSelected = {
                            selectedType = it
                            selectedCategory = null
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AmountInput(
                        amount = amount,
                        onAmountChange = { newAmount ->
                            if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
                                amount = newAmount
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AccountSelector(
                        selectedAccount = selectedAccount,
                        onAccountClick = { showAccountPicker = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CategorySelector(
                        selectedCategory = selectedCategory,
                        selectedType = selectedType,
                        onCategoryClick = { showCategoryPicker = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    NoteInput(
                        note = note,
                        onNoteChange = { note = it }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ActionButtons(
                        amount = amount,
                        selectedAccount = selectedAccount,
                        selectedCategory = selectedCategory,
                        selectedType = selectedType,
                        note = note,
                        onDismiss = onDismiss,
                        onAdd = onAdd
                    )
                }
            }
        }
    }

    if (showAccountPicker) {
        AccountPickerDialog(
            accounts = mockAccounts,
            onAccountSelected = { account ->
                selectedAccount = account
                showAccountPicker = false
            },
            onDismiss = { showAccountPicker = false }
        )
    }

    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = mockCategories[selectedType] ?: emptyList(),
            selectedType = selectedType,
            onCategorySelected = { category ->
                selectedCategory = category
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }
}

@Composable
private fun DialogHeader(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(primaryGreen, primaryGreen.copy(alpha = 0.8f))
                ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Add Transaction",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Track your financial activity",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.background(
                    Color.White.copy(alpha = 0.2f),
                    CircleShape
                )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Text(
        text = "Transaction Type",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionType.values().forEach { type ->
            TransactionTypeCard(
                type = type,
                isSelected = selectedType == type,
                onSelected = onTypeSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TransactionTypeCard(
    type: TransactionType,
    isSelected: Boolean,
    onSelected: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor, borderColor) = getTypeColors(type, isSelected)

    AnimatedVisibility(
        visible = true,
        enter = scaleIn() + fadeIn()
    ) {
        Card(
            modifier = modifier
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onSelected(type) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getTypeIcon(type),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = type.displayName,
                    color = contentColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Text(
        text = "Amount",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        placeholder = {
            Text(
                "0.00",
                color = Color(0xFF9CA3AF),
                fontSize = 18.sp
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryGreen,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = lightGreen.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.Transparent
        ),
        leadingIcon = {
            Box(
                modifier = Modifier
                    .background(
                        primaryGreen.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "$",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }
        },
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
private fun AccountSelector(
    selectedAccount: Account?,
    onAccountClick: () -> Unit
) {
    Text(
        text = "From Account",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    SelectionCard(
        isSelected = selectedAccount != null,
        onClick = onAccountClick
    ) {
        if (selectedAccount != null) {
            AccountItem(account = selectedAccount, modifier = Modifier.weight(1f))
        } else {
            DefaultSelection(
                icon = Icons.Default.Star,
                text = "Select account",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategorySelector(
    selectedCategory: Category?,
    selectedType: TransactionType,
    onCategoryClick: () -> Unit
) {
    Text(
        text = "Category",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    SelectionCard(
        isSelected = selectedCategory != null,
        selectedType = selectedType,
        onClick = onCategoryClick
    ) {
        if (selectedCategory != null) {
            CategoryItem(category = selectedCategory, modifier = Modifier.weight(1f))
        } else {
            DefaultSelection(
                icon = Icons.Default.Star,
                text = "Select category",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SelectionCard(
    isSelected: Boolean,
    selectedType: TransactionType? = null,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                when (selectedType) {
                    TransactionType.INCOME -> lightGreen.copy(alpha = 0.3f)
                    TransactionType.EXPENSE -> lightRed.copy(alpha = 0.3f)
                    TransactionType.TRANSFER -> lightBlue.copy(alpha = 0.3f)
                    else -> lightGreen.copy(alpha = 0.3f)
                }
            } else Color(0xFFF9FAFB)
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) {
                when (selectedType) {
                    TransactionType.INCOME -> primaryGreen.copy(alpha = 0.3f)
                    TransactionType.EXPENSE -> expenseRed.copy(alpha = 0.3f)
                    TransactionType.TRANSFER -> transferBlue.copy(alpha = 0.3f)
                    else -> primaryGreen.copy(alpha = 0.3f)
                }
            } else Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Star,
                contentDescription = "Select",
                tint = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun AccountItem(account: Account, modifier: Modifier = Modifier) {
    IconBox(
        color = Color(android.graphics.Color.parseColor(account.color)),
        icon = getAccountIcon(account.accountType)
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = modifier) {
        Text(
            text = account.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF111827)
        )
        Text(
            text = "$${String.format("%.2f", account.balance)}",
            fontSize = 14.sp,
            color = if (account.balance >= 0) primaryGreen else expenseRed,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CategoryItem(category: Category, modifier: Modifier = Modifier) {
    IconBox(
        color = Color(android.graphics.Color.parseColor(category.color)),
        icon = getCategoryIcon(category.iconName)
    )

    Spacer(modifier = Modifier.width(16.dp))

    Text(
        text = category.name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color(0xFF111827),
        modifier = modifier
    )
}

@Composable
private fun DefaultSelection(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Icon(
        icon,
        contentDescription = null,
        tint = Color(0xFF9CA3AF),
        modifier = Modifier.size(24.dp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = text,
        color = Color(0xFF6B7280),
        fontSize = 16.sp,
    modifier = modifier
    )
}

@Composable
private fun IconBox(
    color: Color,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun NoteInput(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Text(
        text = "Note (Optional)",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier.padding(bottom = 8.dp)
    )

    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        placeholder = {
            Text(
                "Add a note or description...",
                color = Color(0xFF9CA3AF)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryGreen,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = lightGreen.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
private fun ActionButtons(
    amount: String,
    selectedAccount: Account?,
    selectedCategory: Category?,
    selectedType: TransactionType,
    note: String,
    onDismiss: () -> Unit,
    onAdd: (Double, TransactionType, Long, Long, String) -> Unit
) {
    val isEnabled = amount.toDoubleOrNull() != null &&
            amount.toDoubleOrNull() != 0.0 &&
            selectedAccount != null &&
            selectedCategory != null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color(0xFFE5E7EB)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF6B7280)
            )
        ) {
            Text(
                "Cancel",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && amountValue > 0 &&
                    selectedAccount != null && selectedCategory != null) {
                    onAdd(amountValue, selectedType, selectedAccount.id, selectedCategory.id, note)
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            enabled = isEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = getTypeColor(selectedType),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE5E7EB),
                disabledContentColor = Color(0xFF9CA3AF)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (isEnabled) 6.dp else 0.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Add Transaction",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun AccountPickerDialog(
    accounts: List<Account>,
    onAccountSelected: (Account) -> Unit,
    onDismiss: () -> Unit
) {
    PickerDialog(
        title = "Select Account",
        onDismiss = onDismiss
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(accounts) { account ->
                PickerItem(
                    onClick = { onAccountSelected(account) }
                ) {
                    AccountItem(account = account)
                    Text(
                        text = "$${String.format("%.2f", account.balance)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (account.balance >= 0) primaryGreen else expenseRed
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPickerDialog(
    categories: List<Category>,
    selectedType: TransactionType,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    PickerDialog(
        title = "Select Category",
        subtitle = "${selectedType.displayName} Categories",
        onDismiss = onDismiss
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(categories) { category ->
                PickerItem(
                    onClick = { onCategorySelected(category) }
                ) {
                    CategoryItem(category = category)
                }
            }
        }
    }
}

@Composable
private fun PickerDialog(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.padding(bottom = if (subtitle != null) 4.dp else 16.dp)
                )

                subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                content()

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color(0xFF6B7280))
                }
            }
        }
    }
}

@Composable
private fun PickerItem(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FAFB)
        ),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

// Helper functions
private fun getTypeColors(type: TransactionType, isSelected: Boolean): Triple<Color, Color, Color> {
    return when {
        isSelected && type == TransactionType.INCOME -> Triple(primaryGreen, Color.White, primaryGreen)
        isSelected && type == TransactionType.EXPENSE -> Triple(expenseRed, Color.White, expenseRed)
        isSelected && type == TransactionType.TRANSFER -> Triple(transferBlue, Color.White, transferBlue)
        else -> Triple(Color.Transparent, Color(0xFF6B7280), Color(0xFFE5E7EB))
    }
}

private fun getTypeIcon(type: TransactionType): ImageVector {
    return when (type) {
        TransactionType.INCOME -> Icons.Default.Star
        TransactionType.EXPENSE -> Icons.Default.Star
        TransactionType.TRANSFER -> Icons.Default.Star
    }
}

private fun getTypeColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.INCOME -> primaryGreen
        TransactionType.EXPENSE -> expenseRed
        TransactionType.TRANSFER -> transferBlue
    }
}

private fun getAccountIcon(accountType: AccountType): ImageVector {
    return when (accountType) {
        AccountType.CASH -> Icons.Default.Star
        AccountType.BANK_ACCOUNT -> Icons.Default.Star
        AccountType.CHECKING_ACCOUNT -> Icons.Default.Star
        AccountType.SAVINGS -> Icons.Default.Star
        AccountType.CREDIT_CARD -> Icons.Default.Star
        AccountType.DEBIT_CARD -> Icons.Default.Star
        AccountType.INVESTMENT -> Icons.Default.Star
        AccountType.RETIREMENT -> Icons.Default.Star
        AccountType.LOAN -> Icons.Default.Star
        AccountType.MORTGAGE -> Icons.Default.Home
        AccountType.E_WALLET -> Icons.Default.Phone
        AccountType.PREPAID_CARD -> Icons.Default.Star
        AccountType.CRYPTO -> Icons.Default.Star
        AccountType.BUSINESS -> Icons.Default.Star
        AccountType.JOINT_ACCOUNT -> Icons.Default.Star
        AccountType.GIFT_CARD -> Icons.Default.Star
        AccountType.OTHER -> Icons.Default.Star
    }
}

private fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "restaurant" -> Icons.Default.Star
        "car" -> Icons.Default.Star
        "movie" -> Icons.Default.Star
        "shopping" -> Icons.Default.Star
        "receipt" -> Icons.Default.Star
        "work" -> Icons.Default.Star
        "laptop" -> Icons.Default.Star
        "trending_up" -> Icons.Default.Star
        "gift" -> Icons.Default.Star
        "savings" -> Icons.Default.Star
        "medical" -> Icons.Default.Star
        "school" -> Icons.Default.Star
        "star" -> Icons.Default.Star
        "payment" -> Icons.Default.Star
        else -> Icons.Default.Star
    }
}

private fun getMockAccounts(): List<Account> {
    return listOf(
        Account(1, "Cash Wallet", AccountType.CASH, 1547.50, 1500.0, "USD", "cash", "#10B981"),
        Account(2, "Chase Checking", AccountType.BANK_ACCOUNT, 5247.30, 5000.0, "USD", "bank", "#3B82F6"),
        Account(3, "Visa Credit", AccountType.CREDIT_CARD, -847.20, 0.0, "USD", "credit_card", "#EF4444"),
        Account(4, "Savings Goal", AccountType.SAVINGS, 12450.75, 12000.0, "USD", "savings", "#8B5CF6")
    )
}

private fun getMockCategories(): Map<TransactionType, List<Category>> {
    return mapOf(
        TransactionType.EXPENSE to listOf(
            Category(1, "Food & Dining", CategoryType.EXPENSE, "restaurant", "#FF6B6B"),
            Category(2, "Transportation", CategoryType.EXPENSE, "car", "#4ECDC4"),
            Category(3, "Entertainment", CategoryType.EXPENSE, "movie", "#45B7D1"),
            Category(4, "Shopping", CategoryType.EXPENSE, "shopping", "#96CEB4"),
            Category(5, "Bills & Utilities", CategoryType.EXPENSE, "receipt", "#FFEAA7"),
            Category(12, "Healthcare", CategoryType.EXPENSE, "medical", "#FF7675"),
            Category(13, "Education", CategoryType.EXPENSE, "school", "#6C5CE7")
        ),
        TransactionType.INCOME to listOf(
            Category(6, "Salary", CategoryType.INCOME, "work", "#00B894"),
            Category(7, "Freelance", CategoryType.INCOME, "laptop", "#6C5CE7"),
            Category(8, "Investment Returns", CategoryType.INCOME, "trending_up", "#00B894"),
            Category(9, "Gift Received", CategoryType.INCOME, "gift", "#E17055"),
            Category(14, "Bonus", CategoryType.INCOME, "star", "#FDCB6E")
        ),
        TransactionType.TRANSFER to listOf(
            Category(10, "Savings Transfer", CategoryType.SAVINGS, "savings", "#00B894"),
            Category(11, "Investment", CategoryType.SAVINGS, "trending_up", "#6C5CE7"),
            Category(15, "Debt Payment", CategoryType.SAVINGS, "payment", "#E17055")
        )
    )
}