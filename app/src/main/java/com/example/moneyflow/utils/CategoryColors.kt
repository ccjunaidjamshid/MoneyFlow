package com.example.moneyflow.utils

import androidx.compose.ui.graphics.Color

/**
 * Utility object containing predefined colors for categories
 */
object CategoryColors {
    
    // Income colors (green shades)
    val incomeColors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF8BC34A), // Light Green
        Color(0xFF66BB6A), // Green 400
        Color(0xFF43A047), // Green 600
        Color(0xFF2E7D32), // Green 800
        Color(0xFF00C853), // Green A400
        Color(0xFF69F0AE), // Green A200
        Color(0xFF00E676)  // Green A400
    )
    
    // Expense colors (red/orange shades)
    val expenseColors = listOf(
        Color(0xFFF44336), // Red
        Color(0xFFE53935), // Red 600
        Color(0xFFD32F2F), // Red 700
        Color(0xFFFF5722), // Deep Orange
        Color(0xFFFF7043), // Deep Orange 400
        Color(0xFFFF6F00), // Orange A700
        Color(0xFFFF9800), // Orange
        Color(0xFFFFA726)  // Orange 400
    )
    
    // Savings colors (blue shades)
    val savingsColors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF1976D2), // Blue 700
        Color(0xFF1565C0), // Blue 800
        Color(0xFF0D47A1), // Blue 900
        Color(0xFF42A5F5), // Blue 400
        Color(0xFF64B5F6), // Blue 300
        Color(0xFF2979FF), // Blue A400
        Color(0xFF448AFF)  // Blue A200
    )
    
    // Neutral colors for other categories
    val neutralColors = listOf(
        Color(0xFF9E9E9E), // Grey
        Color(0xFF607D8B), // Blue Grey
        Color(0xFF795548), // Brown
        Color(0xFF9C27B0), // Purple
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF00BCD4), // Cyan
        Color(0xFF009688)  // Teal
    )
    
    // All colors combined
    val allColors = incomeColors + expenseColors + savingsColors + neutralColors
    
    /**
     * Get colors for specific category type
     */
    fun getColorsForType(type: String): List<Color> {
        return when (type.lowercase()) {
            "income" -> incomeColors
            "expense" -> expenseColors
            "savings" -> savingsColors
            else -> neutralColors
        }
    }
    
    /**
     * Get all default colors for UI selection
     */
    fun getDefaultColors(): List<Color> = allColors
    
    /**
     * Get default color for category type
     */
    fun getDefaultColor(type: String): Color {
        return when (type.lowercase()) {
            "income" -> incomeColors[0]
            "expense" -> expenseColors[0]
            "savings" -> savingsColors[0]
            else -> neutralColors[0]
        }
    }
    
    /**
     * Convert Color to hex string
     */
    fun colorToHex(color: Color): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", red, green, blue)
    }
    
    /**
     * Convert hex string to Color
     */
    fun hexToColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            neutralColors[0] // Default color if parsing fails
        }
    }
}
