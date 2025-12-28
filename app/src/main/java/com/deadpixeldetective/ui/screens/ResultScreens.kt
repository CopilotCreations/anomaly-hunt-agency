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
 * Level complete celebration screen.
 */
@Composable
fun LevelCompleteScreen(
    score: Int,
    levelNumber: Int,
    onNextLevel: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        SuccessGreen.copy(alpha = 0.1f),
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Level Complete!",
                style = MaterialTheme.typography.displaySmall,
                color = SuccessGreen,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Level $levelNumber cleared",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayMedium,
                        color = SecondaryVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen
                )
            ) {
                Text(
                    text = "Next Level",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            TextButton(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to Menu",
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Game over screen.
 */
@Composable
fun GameOverScreen(
    score: Int,
    levelNumber: Int,
    onTryAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        AnomalyHighlight.copy(alpha = 0.1f),
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Game Over",
                style = MaterialTheme.typography.displaySmall,
                color = AnomalyHighlight,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Level $levelNumber",
                style = MaterialTheme.typography.headlineSmall,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Final Score",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary
                    )
                }
            }
            
            Text(
                text = "The anomaly was hidden well...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onTryAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryVariant
                )
            ) {
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            TextButton(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to Menu",
                    color = TextSecondary
                )
            }
        }
    }
}
