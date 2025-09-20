package com.example.moneyflow.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Beautiful Light Green Color Palette for MoneyFlow
 * Primary: Very Light Green theme
 * Expense: Light Red
 * Savings: Light Blue
 * Background: Pure White
 */
object AppColors {
    // Primary Colors - Slightly Darker Light Green Theme
    val Primary = Color(0xFF66BB6A) // Medium light green (darker than before)
    val PrimaryVariant = Color(0xFF4CAF50) // Darker green
    val Secondary = Color(0xFF81C784) // Light green (was primary before)
    val SecondaryLight = Color(0xFFA5D6A7) // Medium light green
    val PrimaryDark = Color(0xFF388E3C) // Dark green for contrast
    
    // Income Colors - Green family (slightly darker)
    val IncomeLight = Color(0xFF66BB6A) // Medium light green for income
    val Income = Color(0xFF4CAF50) // Standard green for income
    val IncomeDark = Color(0xFF2E7D32) // Darker green for income
    
    // Expense Colors - Slightly Darker Red family  
    val ExpenseLight = Color(0xFFE57373) // Medium light red for expenses (darker than before)
    val Expense = Color(0xFFEF5350) // Standard red
    val ExpenseDark = Color(0xFFD32F2F) // Darker red for contrast
    
    // Savings Colors - Slightly Darker Blue family
    val SavingsLight = Color(0xFF64B5F6) // Medium light blue for savings (darker than before)
    val Savings = Color(0xFF42A5F5) // Standard blue for savings
    val SavingsDark = Color(0xFF1565C0) // Darker blue for contrast
    
    // Background and Surface
    val Background = Color.White // Pure white background
    val Surface = Color.White // Pure white surface
    val CardBackground = Color(0xFFF5F5F5) // Slightly more visible gray for cards
    
    // Text Colors
    val OnSurface = Color(0xFF1B1B1B) // Dark text
    val TextSecondary = Color(0xFF424242) // Darker gray text (was lighter before)
    val TextLight = Color(0xFF616161) // Medium gray text (was lighter before)
    
    // Accent Colors
    val Accent = Color(0xFFA5D6A7) // Light green accent (slightly darker)
    val Success = Color(0xFF66BB6A) // Success green (darker)
    val Warning = Color(0xFFFF9800) // Slightly darker orange for warnings
    val Error = Color(0xFFEF5350) // Medium red for errors
    
    // Special UI Colors
    val Divider = Color(0xFFE0E0E0) // Light gray divider
    val Shadow = Color(0x1A000000) // Subtle shadow
}
