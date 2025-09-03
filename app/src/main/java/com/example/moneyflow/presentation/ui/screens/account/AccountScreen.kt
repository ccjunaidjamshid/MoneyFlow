package com.example.moneyflow.presentation.ui.screens.account

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.moneyflow.R
import com.example.moneyflow.data.model.Account
import com.example.moneyflow.presentation.ui.components.CreateAccountDialog
import com.example.moneyflow.presentation.ui.components.EditAccountDialog
import com.example.moneyflow.presentation.ui.components.DeleteAccountDialog
import com.example.moneyflow.presentation.ui.components.AccountActionMenu
import com.example.moneyflow.presentation.viewmodel.AccountViewModel
import com.example.moneyflow.ui.theme.AccountColors
import com.example.moneyflow.ui.theme.AccountIcons
import java.text.NumberFormat
import java.util.*

// Consistent Color Palette - Same as HomeScreen
object AppColors {
    val Primary = Color(0xFF2E7D32) // Dark green
    val PrimaryVariant = Color(0xFF388E3C) // Deep green
    val Secondary = Color(0xFF4CAF50) // Medium green
    val SecondaryLight = Color(0xFF66BB6A) // Light green
    val Accent = Color(0xFFA8E6CF) // Light mint green
    val Background = Color(0xFFF5F8F5) // Light neutral background
    val Surface = Color.White
    val OnSurface = Color(0xFF1B1B1B)
    val TextSecondary = Color(0xFF5F6368)
    val Error = Color(0xFFE57373)
    val Success = Color(0xFF81C784)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    val hapticFeedback = LocalHapticFeedback.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()

    // Animation state
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Show success message
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearSuccessMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header with Create Button
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
                        text = "My Accounts",
                        fontSize = if (isTablet) 26.sp else 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                    Text(
                        text = "Total Balance: ${formatCurrency(totalBalance)}",
                        fontSize = if (isTablet) 16.sp else 14.sp,
                        color = AppColors.Secondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${accounts.size} account${if (accounts.size != 1) "s" else ""}",
                        fontSize = if (isTablet) 13.sp else 11.sp,
                        color = AppColors.TextSecondary
                    )
                }

                FloatingActionButton(
                    onClick = { viewModel.showCreateAccountDialog() },
                    modifier = Modifier.size(if (isTablet) 56.dp else 48.dp),
                    containerColor = AppColors.Primary,
                    contentColor = AppColors.Surface,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Account",
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

        // Account List
        if (accounts.isEmpty()) {
            // Empty State
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
                        painter = painterResource(id = com.example.moneyflow.R.drawable.wallet),
                        contentDescription = "No accounts",
                        modifier = Modifier.size(80.dp),
                        tint = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No accounts yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.OnSurface
                    )
                    Text(
                        text = "Create your first account to get started",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.showCreateAccountDialog() },
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
                        Text("Create Account")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accounts) { account ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 2 }
                        )
                    ) {
                        AccountItem(
                            account = account,
                            isTablet = isTablet,
                            onLongClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.showAccountActionMenu(account)
                            },
                            onClick = {
                                viewModel.showEditAccountDialog(account)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs remain the same...
    // Create Account Dialog
    if (uiState.showCreateDialog) {
        CreateAccountDialog(
            form = uiState.createAccountForm,
            isCreating = uiState.isCreating,
            error = uiState.createError,
            onDismiss = { viewModel.hideCreateAccountDialog() },
            onNameChange = { viewModel.updateAccountName(it) },
            onTypeChange = { viewModel.updateAccountType(it) },
            onBalanceChange = { viewModel.updateInitialBalance(it) },
            onIconChange = { viewModel.updateAccountIcon(it) },
            onColorChange = { viewModel.updateAccountColor(it) },
            onDescriptionChange = { viewModel.updateAccountDescription(it) },
            onCreateClick = { viewModel.createAccount() }
        )
    }

    // Edit Account Dialog
    if (uiState.showEditDialog) {
        EditAccountDialog(
            form = uiState.editAccountForm,
            isEditing = uiState.isEditing,
            error = uiState.editError,
            onDismiss = { viewModel.hideEditAccountDialog() },
            onNameChange = { viewModel.updateEditAccountName(it) },
            onTypeChange = { viewModel.updateEditAccountType(it) },
            onBalanceChange = { viewModel.updateEditInitialBalance(it) },
            onIconChange = { viewModel.updateEditAccountIcon(it) },
            onColorChange = { viewModel.updateEditAccountColor(it) },
            onDescriptionChange = { viewModel.updateEditAccountDescription(it) },
            onUpdateClick = { viewModel.updateAccount() }
        )
    }

    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog && uiState.accountToDelete != null) {
        DeleteAccountDialog(
            account = uiState.accountToDelete!!,
            isDeleting = uiState.isDeleting,
            error = uiState.deleteError,
            onDismiss = { viewModel.hideDeleteConfirmDialog() },
            onConfirmDelete = { viewModel.deleteAccount() }
        )
    }

    // Account Action Menu
    if (uiState.showActionMenu && uiState.selectedAccount != null) {
        AccountActionMenu(
            account = uiState.selectedAccount!!,
            onDismiss = { viewModel.hideAccountActionMenu() },
            onEditClick = { viewModel.showEditAccountDialog(uiState.selectedAccount!!) },
            onDeleteClick = { viewModel.showDeleteConfirmDialog(uiState.selectedAccount!!) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountItem(
    account: Account,
    isTablet: Boolean,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val accountIconRes = AccountIcons.getDrawableResourceByName(account.icon)
    val accountColor = AccountColors.getColorByHex(account.color)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
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
            // Account Icon
            Box(
                modifier = Modifier
                    .size(if (isTablet) 52.dp else 48.dp)
                    .clip(CircleShape)
                    .background(accountColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = accountIconRes),
                    contentDescription = account.name,
                    modifier = Modifier.size(if (isTablet) 24.dp else 22.dp),
                    tint = accountColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Account Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = account.name,
                    fontSize = if (isTablet) 17.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.OnSurface
                )

                Text(
                    text = account.accountType.displayName,
                    fontSize = if (isTablet) 13.sp else 11.sp,
                    color = AppColors.TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                if (!account.description.isNullOrBlank()) {
                    Text(
                        text = account.description,
                        fontSize = if (isTablet) 11.sp else 10.sp,
                        color = AppColors.TextSecondary.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }

            // Balance and Change Indicator
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatCurrency(account.balance),
                    fontSize = if (isTablet) 17.sp else 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (account.balance >= 0)
                        AppColors.Primary
                    else
                        AppColors.Error
                )

                if (account.balance != account.initialBalance) {
                    val change = account.balance - account.initialBalance
                    Surface(
                        color = if (change >= 0)
                            AppColors.Success.copy(alpha = 0.1f)
                        else
                            AppColors.Error.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${if (change >= 0) "+" else ""}${formatCurrency(change)}",
                            fontSize = if (isTablet) 11.sp else 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (change >= 0)
                                AppColors.Success
                            else
                                AppColors.Error,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to format currency
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(amount)
}