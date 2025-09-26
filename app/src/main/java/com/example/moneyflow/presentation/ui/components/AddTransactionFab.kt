package com.example.moneyflow.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AddTransactionFab(
    onClick: () -> Unit
) {
    // Simple green like the homepage card header
    val MoneyGreen = Color(0xFF22C55E) // adjust to your exact hex if needed

    FloatingActionButton(
        onClick = onClick,
        containerColor = MoneyGreen,     // direct color (no theme)
        contentColor = Color.White,      // simple, high-contrast icon color
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        ),
        modifier = Modifier.semantics {
            this.contentDescription = "Add transaction"
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null // semantics handles this
        )
    }
}