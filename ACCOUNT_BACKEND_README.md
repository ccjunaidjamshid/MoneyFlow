# MoneyFlow Account Backend Implementation

## Overview
This document outlines the complete backend implementation for the Account system in the MoneyFlow expense tracker application. The implementation follows Clean Architecture principles with proper separation of concerns.

## Architecture

### 1. **Data Layer**
- **Account.kt**: Data class representing the account entity
- **AccountDao.kt**: Room database interface for account operations
- **MoneyFlowDatabase.kt**: Room database configuration
- **Converters.kt**: Type converters for custom data types
- **AccountRepositoryImpl.kt**: Implementation of the repository interface

### 2. **Domain Layer**
- **AccountRepository.kt**: Repository interface defining data operations
- **Use Cases**: Business logic implementations
  - CreateAccountUseCase
  - GetAllAccountsUseCase
  - UpdateAccountUseCase
  - DeleteAccountUseCase
  - GetTotalBalanceUseCase
  - InitializeDefaultAccountsUseCase

### 3. **Dependency Injection**
- **DatabaseModule.kt**: Dagger Hilt module for database dependencies
- **MoneyFlowApplication.kt**: Application class with Hilt initialization

## Account Model Features

### Account Properties
- `id`: Unique identifier (auto-generated)
- `name`: Account name (e.g., "Cash", "Bank Account")
- `accountType`: Enum defining account type
- `balance`: Current account balance
- `initialBalance`: Starting balance when account was created
- `currency`: Currency code (default: USD)
- `icon`: Icon identifier for UI display
- `color`: Theme color for the account
- `description`: Optional account description
- `isActive`: Whether account is active or archived
- `createdAt`: Account creation timestamp
- `updatedAt`: Last modification timestamp
- `includeInTotalBalance`: Whether to include in total balance calculations

### Account Types
- CASH
- BANK_ACCOUNT
- CREDIT_CARD
- DEBIT_CARD
- SAVINGS
- INVESTMENT
- LOAN
- E_WALLET
- PREPAID_CARD
- OTHER

## Database Operations

### Available Operations
1. **Create**: Add new accounts with validation
2. **Read**: Get accounts by various filters
3. **Update**: Modify account information
4. **Delete**: Remove accounts (with safety checks)
5. **Balance Management**: Update account balances
6. **Search**: Find accounts by name or description
7. **Statistics**: Get account summaries and statistics

### Key Queries
- Get all active accounts
- Get accounts by type
- Calculate total balance
- Search accounts
- Get account statistics with transaction counts

## Business Logic (Use Cases)

### Validation Rules
- Account name cannot be empty
- Account name must be unique
- Account name limited to 50 characters
- Cannot delete accounts with existing transactions

### Default Accounts
The system creates three default accounts on first launch:
1. **Cash** - For cash transactions
2. **Bank Account** - For bank-related transactions
3. **Credit Card** - For credit card transactions

## Database Schema

```sql
CREATE TABLE accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    accountType TEXT NOT NULL,
    balance REAL NOT NULL,
    initialBalance REAL NOT NULL,
    currency TEXT NOT NULL DEFAULT 'USD',
    icon TEXT NOT NULL,
    color TEXT NOT NULL,
    description TEXT,
    isActive INTEGER NOT NULL DEFAULT 1,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    includeInTotalBalance INTEGER NOT NULL DEFAULT 1
);
```

## Usage Examples

### Creating an Account
```kotlin
val account = Account(
    name = "My Savings",
    accountType = AccountType.SAVINGS,
    balance = 1000.0,
    initialBalance = 1000.0,
    icon = "savings",
    color = "#4CAF50",
    description = "Emergency savings account"
)

val result = createAccountUseCase(account)
```

### Getting All Accounts
```kotlin
val accounts = getAllAccountsUseCase()
    .collect { accountList ->
        // Handle account list
    }
```

### Updating Balance
```kotlin
accountRepository.addToAccountBalance(accountId, 500.0)
```

## Dependencies Added

### build.gradle.kts (app level)
```kotlin
// Room dependencies
implementation("androidx.room:room-runtime:2.6.0")
implementation("androidx.room:room-ktx:2.6.0")
kapt("androidx.room:room-compiler:2.6.0")

// Dagger Hilt dependencies
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// ViewModel Compose
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

## Next Steps

To complete the account implementation, you'll need to:

1. **Create ViewModel**: Implement AccountViewModel to handle UI state
2. **Create UI Screens**: Build Compose screens for:
   - Account list display
   - Add/edit account forms
   - Account details view
3. **Integrate with Transaction System**: Connect accounts to transaction creation
4. **Add Error Handling**: Implement proper error display in UI
5. **Add Loading States**: Show loading indicators during operations

## Testing

Consider implementing:
- Unit tests for use cases
- Repository tests with Room testing framework
- Integration tests for database operations
- UI tests for account screens

## File Structure
```
app/src/main/java/com/example/moneyflow/
├── data/
│   ├── database/
│   │   ├── dao/
│   │   │   └── AccountDao.kt
│   │   ├── Converters.kt
│   │   └── MoneyFlowDatabase.kt
│   ├── model/
│   │   └── Account.kt
│   └── repository/
│       └── AccountRepositoryImpl.kt
├── domain/
│   ├── repository/
│   │   └── AccountRepository.kt
│   └── usecase/
│       └── account/
│           ├── CreateAccountUseCase.kt
│           ├── DeleteAccountUseCase.kt
│           ├── GetAllAccountsUseCase.kt
│           ├── GetTotalBalanceUseCase.kt
│           ├── InitializeDefaultAccountsUseCase.kt
│           └── UpdateAccountUseCase.kt
├── di/
│   └── DatabaseModule.kt
├── MainActivity.kt
└── MoneyFlowApplication.kt
```

The backend is now fully implemented and ready for integration with the presentation layer!
