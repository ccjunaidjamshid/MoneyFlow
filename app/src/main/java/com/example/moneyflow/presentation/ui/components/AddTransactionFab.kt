package com.example.moneyflow.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * A custom Floating Action Button for adding transactions
 * Using the mint green colors from the splash screen for consistency
 */
@Composable
fun AddTransactionFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false
) {
    // Using explicit color values from splash screen
    val mintGreen = Color(0xFFA8E6CF)  // Light mint green
    val mintGreenDark = Color(0xFF78C2AD)  // Darker mint green
    
    // Responsive sizing
    val fabSize = if (isTablet) 72.dp else 60.dp
    val iconSize = if (isTablet) 32.dp else 28.dp
    val bottomPadding = if (isTablet) 24.dp else 20.dp
    val endPadding = if (isTablet) 24.dp else 20.dp
    
    var showFab by remember { mutableStateOf(false) }

    // Animation for FAB entry
    LaunchedEffect(Unit) {
        delay(300) // Delay to ensure the animation doesn't happen too soon
        showFab = true
    }
    
    // Elevation animation for a subtle hover effect
    val elevation by animateDpAsState(
        targetValue = if (showFab) if (isTablet) 8.dp else 6.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "FAB Elevation"
    )
    
    // Scale animation for a nice pop effect
    val scale by animateFloatAsState(
        targetValue = if (showFab) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "FAB Scale"
    )
    
    AnimatedVisibility(
        visible = showFab,
        enter = fadeIn(animationSpec = tween(500)) + 
               scaleIn(animationSpec = spring(
                   dampingRatio = Spring.DampingRatioLowBouncy,
                   stiffness = Spring.StiffnessMedium
               ))
    ) {
        // Using colors from the splash screen animation (0xFFA8E6CF and 0xFFDCEDC1)
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .size(fabSize)
                .padding(bottom = bottomPadding, end = endPadding)
                .shadow(
                    elevation = elevation, 
                    shape = CircleShape,
                    spotColor = mintGreen.copy(alpha = 0.4f)
                )
                .scale(scale),
            containerColor = mintGreenDark,  // Explicit mint green color
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = elevation,
                pressedElevation = elevation + 2.dp
            )
        ) {
            // Custom styling for the plus icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF78C2AD),  // Darker mint green explicitly defined
                                Color(0xFFA8E6CF).copy(alpha = 0.8f)  // Light mint green with transparency
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}
