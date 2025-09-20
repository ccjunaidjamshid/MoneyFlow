package com.example.moneyflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TopBar component that is consistent across all screens
 * Uses the mint green colors from the app's theme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyflowTopBar(
    title: String = "Moneyflow",
    modifier: Modifier = Modifier
) {
    // Using the mint green colors from splash screen for consistency
    val mintGreen = Color(0xFFA8E6CF)
    val mintGreenDark = Color(0xFF78C2AD)
    val pastelGreen = Color(0xFFDCEDC1)
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                ambientColor = Color.Gray.copy(alpha = 0.15f),
                spotColor = Color.Gray.copy(alpha = 0.15f)
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Moneyflow",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32), // Use green color for text
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}
