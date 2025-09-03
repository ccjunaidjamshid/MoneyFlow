package com.example.moneyflow.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Predefined colors for account selection
 */
object AccountColors {
    val colors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFF44336), // Red
        Color(0xFF9C27B0), // Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF00BCD4), // Cyan
        Color(0xFF8BC34A), // Light Green
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFF5722), // Deep Orange
        Color(0xFFE91E63), // Pink
        Color(0xFF607D8B), // Blue Grey
        Color(0xFF795548), // Brown
        Color(0xFF009688), // Teal
        Color(0xFFCDDC39), // Lime
        Color(0xFFFFC107), // Amber
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF1976D2), // Blue 700
        Color(0xFF388E3C), // Green 700
        Color(0xFFF57C00), // Orange 700
        Color(0xFFD32F2F), // Red 700
        Color(0xFF7B1FA2), // Purple 700
        Color(0xFF303F9F), // Indigo 700
        Color(0xFF0097A7), // Cyan 700
    )
    
    fun getColorByHex(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            colors.first()
        }
    }
    
    fun getHexFromColor(color: Color): String {
        val alpha = (color.alpha * 255).toInt()
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        return String.format("#%02X%02X%02X", red, green, blue)
    }
}
