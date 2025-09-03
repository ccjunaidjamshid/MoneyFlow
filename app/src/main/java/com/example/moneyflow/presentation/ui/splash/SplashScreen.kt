package com.example.moneyflow.presentation.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneyflow.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth >= 600
    
    // Responsive dimensions
    val iconSize = if (isTablet) 180.dp else 130.dp
    val titleFontSize = if (isTablet) 42.sp else 34.sp
    val subtitleFontSize = if (isTablet) 20.sp else 16.sp
    val footerFontSize = if (isTablet) 16.sp else 12.sp
    val horizontalPadding = if (isTablet) 48.dp else 24.dp
    val topSpacer = if (isTablet) 80.dp else 60.dp
    
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        startAnimation = true
    }

    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor1 by infiniteTransition.animateColor(
        initialValue = Color(0xFFA8E6CF), // Light mint green
        targetValue = Color(0xFFDCEDC1), // Soft pastel green
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedColor2 by infiniteTransition.animateColor(
        initialValue = Color.White,
        targetValue = Color(0xFFF1F8E9), // Very light greenish white
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scaleInitial = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "InitialScale"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alpha = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "AlphaAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(animatedColor1, animatedColor2)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(topSpacer))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wallet),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(iconSize)
                        .scale(scaleInitial.value * pulseScale)
                        .alpha(alpha.value)
                )

                Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 20.dp))

                AnimatedVisibility(
                    visible = startAnimation,
                    enter = fadeIn(tween(800)) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    )
                ) {
                    Text(
                        text = "MoneyFlow",
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF388E3C), // Deep green for contrast
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.15f),
                                blurRadius = 4f
                            )
                        )
                    )
                }

                Spacer(modifier = Modifier.height(if (isTablet) 16.dp else 10.dp))

                AnimatedVisibility(
                    visible = startAnimation,
                    enter = fadeIn(tween(1000, delayMillis = 200))
                ) {
                    Text(
                        text = "Track Smart. Spend Wise.",
                        fontSize = subtitleFontSize,
                        color = Color(0xFF4CAF50), // Medium green
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
            }

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1200, delayMillis = 400))
            ) {
                Text(
                    text = "Developed by Meta",
                    fontSize = footerFontSize,
                    color = Color(0xFF2E7D32), // Dark green
                    modifier = Modifier.padding(bottom = if (isTablet) 24.dp else 16.dp)
                )
            }
        }
    }
}