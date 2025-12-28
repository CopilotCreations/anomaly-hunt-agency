package com.deadpixeldetective.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.deadpixeldetective.ui.theme.*

/**
 * Main menu screen.
 */
@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onAboutClick: () -> Unit,
    highScore: Int,
    highestLevel: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu_bg")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkSurface.copy(alpha = 0.3f + gradientOffset * 0.2f),
                        DarkBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Dead Pixel",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Detective",
                style = MaterialTheme.typography.displaySmall,
                color = SecondaryVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle with flicker effect
            val flickerAlpha by infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "flicker"
            )
            
            Text(
                text = "Find the anomalies",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary.copy(alpha = flickerAlpha),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Play button
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryVariant
                )
            ) {
                Text(
                    text = "PLAY",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            // Settings button
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // How to Play button
            TextButton(
                onClick = onHowToPlayClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "How to Play",
                    color = TextSecondary
                )
            }
            
            // About button
            TextButton(
                onClick = onAboutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "About",
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats
            if (highScore > 0 || highestLevel > 1) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(0.7f)
                ) {
                    Text(
                        text = "High Score: $highScore",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Highest Level: $highestLevel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
