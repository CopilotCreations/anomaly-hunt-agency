package com.deadpixeldetective.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.deadpixeldetective.model.*
import com.deadpixeldetective.ui.theme.*
import com.deadpixeldetective.viewmodel.GameViewModel
import kotlin.math.sin

/**
 * Main game screen with anomaly canvas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    userPreferences: UserPreferences,
    onLevelComplete: () -> Unit,
    onGameOver: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val sensorState by viewModel.sensorState.collectAsState()
    
    // Animation for time-based anomalies
    val infiniteTransition = rememberInfiniteTransition(label = "game_anim")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (userPreferences.reducedMotion) 10000 else 3000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )
    
    LaunchedEffect(animationProgress) {
        viewModel.updateAnimationProgress(animationProgress)
    }
    
    // Navigate on level complete or game over
    LaunchedEffect(gameState.isLevelComplete, gameState.isGameOver) {
        if (gameState.isLevelComplete) {
            onLevelComplete()
        } else if (gameState.isGameOver) {
            onGameOver()
        }
    }
    
    val level = gameState.currentLevel
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Game canvas
        if (level != null) {
            GameCanvas(
                level = level,
                gameState = gameState,
                sensorState = sensorState,
                animationProgress = animationProgress,
                userPreferences = userPreferences,
                onTap = { x, y -> viewModel.onTap(x, y) },
                getAnomalyVisibility = { viewModel.getAnomalyVisibility(it) }
            )
        }
        
        // HUD overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToMenu) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to menu",
                        tint = TextPrimary
                    )
                }
                
                level?.let {
                    Text(
                        text = "Level ${it.number}",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                }
                
                Text(
                    text = "Score: ${gameState.score}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Attempts indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(level?.maxAttempts ?: 0) { index ->
                    val isUsed = index >= gameState.attemptsRemaining
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .padding(2.dp)
                            .background(
                                color = if (isUsed) TextSecondary.copy(alpha = 0.3f) else SecondaryVariant,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
            }
            
            // Hint display
            if (userPreferences.showHints && level != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HintDisplay(level = level, sensorState = sensorState)
            }
        }
        
        // Found count
        level?.let {
            Text(
                text = "${gameState.foundAnomalies.size}/${it.anomalies.size} found",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

/**
 * The main game canvas where anomalies are rendered.
 */
@Composable
private fun GameCanvas(
    level: Level,
    gameState: GameState,
    sensorState: SensorState,
    animationProgress: Float,
    userPreferences: UserPreferences,
    onTap: (Float, Float) -> Unit,
    getAnomalyVisibility: (Anomaly) -> Float
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Game area - tap to find anomalies" }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        val normalizedX = offset.x / canvasSize.width
                        val normalizedY = offset.y / canvasSize.height
                        onTap(normalizedX, normalizedY)
                    }
                }
            }
    ) {
        canvasSize = size
        
        // Draw background with subtle noise pattern
        drawBackgroundPattern(animationProgress, userPreferences.reducedMotion)
        
        // Draw anomalies
        for (anomaly in level.anomalies) {
            if (anomaly.id !in gameState.foundAnomalies) {
                val visibility = getAnomalyVisibility(anomaly)
                drawAnomaly(anomaly, visibility, animationProgress, userPreferences.highContrastMode)
            }
        }
        
        // Draw found anomaly indicators
        for (anomalyId in gameState.foundAnomalies) {
            val anomaly = level.anomalies.find { it.id == anomalyId } ?: continue
            drawFoundIndicator(anomaly)
        }
        
        // Draw heatmap if enabled
        if (gameState.showHeatmap) {
            drawHeatmap(gameState.tapHistory)
        }
        
        // Draw tap feedback
        for (tap in gameState.tapHistory.takeLast(3)) {
            drawTapFeedback(tap)
        }
    }
}

private fun DrawScope.drawBackgroundPattern(animationProgress: Float, reducedMotion: Boolean) {
    val patternDensity = 50
    val variation = if (reducedMotion) 0f else sin(animationProgress * 2 * Math.PI.toFloat()) * 0.02f
    
    for (x in 0 until patternDensity) {
        for (y in 0 until patternDensity) {
            val noiseValue = ((x * 7 + y * 13) % 100) / 100f
            val alpha = 0.01f + noiseValue * 0.02f + variation * noiseValue
            
            drawRect(
                color = Color.White.copy(alpha = alpha.coerceIn(0f, 0.05f)),
                topLeft = Offset(
                    x * size.width / patternDensity,
                    y * size.height / patternDensity
                ),
                size = Size(
                    size.width / patternDensity,
                    size.height / patternDensity
                )
            )
        }
    }
}

private fun DrawScope.drawAnomaly(
    anomaly: Anomaly,
    visibility: Float,
    animationProgress: Float,
    highContrast: Boolean
) {
    val centerX = anomaly.x * size.width
    val centerY = anomaly.y * size.height
    val pixelSize = anomaly.radius * minOf(size.width, size.height)
    
    val baseAlpha = visibility * 0.15f
    
    when (anomaly.type) {
        AnomalyType.PIXEL_OFFSET -> {
            // Single offset pixel
            val offsetX = sin(anomaly.animationPhase * Math.PI.toFloat()) * 2f
            drawRect(
                color = if (highContrast) HighContrastAccent.copy(alpha = baseAlpha * 2)
                       else AnomalyPixelOffset.copy(alpha = baseAlpha),
                topLeft = Offset(centerX + offsetX - pixelSize / 2, centerY - pixelSize / 2),
                size = Size(pixelSize, pixelSize)
            )
        }
        
        AnomalyType.SUBTLE_GRADIENT -> {
            // Barely visible gradient
            val gradientAlpha = baseAlpha * (0.5f + 0.5f * sin(animationProgress * Math.PI.toFloat() * 2))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (highContrast) HighContrastAccent.copy(alpha = gradientAlpha)
                        else AnomalyGradient.copy(alpha = gradientAlpha),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = pixelSize * 2
                ),
                center = Offset(centerX, centerY),
                radius = pixelSize * 2
            )
        }
        
        AnomalyType.TEMPORAL_FLICKER -> {
            // Flickering pixel
            val flickerPhase = (animationProgress + anomaly.animationPhase) % 1f
            val flickerAlpha = if (flickerPhase < 0.3f) baseAlpha * 2 else baseAlpha * 0.2f
            drawRect(
                color = if (highContrast) HighContrastAccent.copy(alpha = flickerAlpha)
                       else AnomalyFlicker.copy(alpha = flickerAlpha),
                topLeft = Offset(centerX - pixelSize / 2, centerY - pixelSize / 2),
                size = Size(pixelSize, pixelSize)
            )
        }
        
        AnomalyType.PIXEL_CLUSTER -> {
            // Small cluster of misaligned pixels
            val clusterOffsets = listOf(
                Offset(0f, 0f),
                Offset(pixelSize, 0f),
                Offset(0f, pixelSize),
                Offset(-pixelSize, 0f),
                Offset(0f, -pixelSize)
            )
            for (offset in clusterOffsets) {
                drawRect(
                    color = if (highContrast) HighContrastAccent.copy(alpha = baseAlpha * 1.5f)
                           else AnomalyCluster.copy(alpha = baseAlpha),
                    topLeft = Offset(
                        centerX + offset.x - pixelSize / 4,
                        centerY + offset.y - pixelSize / 4
                    ),
                    size = Size(pixelSize / 2, pixelSize / 2)
                )
            }
        }
        
        AnomalyType.COLOR_SHIFT -> {
            // Color that shifts subtly
            val hueShift = sin(animationProgress * Math.PI.toFloat() * 2)
            val color = if (highContrast) HighContrastAccent
                       else Color(
                           red = 0.1f + hueShift * 0.05f,
                           green = 0.15f + hueShift * 0.02f,
                           blue = 0.2f - hueShift * 0.03f,
                           alpha = baseAlpha
                       )
            drawCircle(
                color = color,
                center = Offset(centerX, centerY),
                radius = pixelSize
            )
        }
        
        AnomalyType.ROTATION_REVEAL -> {
            // Pattern visible during rotation
            val rotationAlpha = baseAlpha * 1.5f
            drawCircle(
                color = if (highContrast) HighContrastAccent.copy(alpha = rotationAlpha)
                       else AnomalyGradient.copy(alpha = rotationAlpha),
                center = Offset(centerX, centerY),
                radius = pixelSize
            )
        }
        
        AnomalyType.ORIENTATION_DEPENDENT -> {
            // Pattern that appears in specific orientation
            drawRect(
                color = if (highContrast) HighContrastAccent.copy(alpha = baseAlpha * 2)
                       else AnomalyColorShift.copy(alpha = baseAlpha),
                topLeft = Offset(centerX - pixelSize, centerY - pixelSize / 4),
                size = Size(pixelSize * 2, pixelSize / 2)
            )
        }
    }
}

private fun DrawScope.drawFoundIndicator(anomaly: Anomaly) {
    val centerX = anomaly.x * size.width
    val centerY = anomaly.y * size.height
    val radius = anomaly.radius * minOf(size.width, size.height) * 3
    
    drawCircle(
        color = SuccessGreen.copy(alpha = 0.3f),
        center = Offset(centerX, centerY),
        radius = radius
    )
    
    drawCircle(
        color = SuccessGreen.copy(alpha = 0.5f),
        center = Offset(centerX, centerY),
        radius = radius * 0.3f
    )
}

private fun DrawScope.drawTapFeedback(tap: TapData) {
    val centerX = tap.x * size.width
    val centerY = tap.y * size.height
    
    val color = if (tap.wasHit) SuccessGreen else AnomalyHighlight
    val timeSinceTap = System.currentTimeMillis() - tap.timestamp
    val alpha = (1f - (timeSinceTap / 2000f)).coerceIn(0f, 0.5f)
    
    if (alpha > 0) {
        drawCircle(
            color = color.copy(alpha = alpha),
            center = Offset(centerX, centerY),
            radius = 20f + (timeSinceTap / 50f)
        )
    }
}

private fun DrawScope.drawHeatmap(tapHistory: List<TapData>) {
    for (tap in tapHistory) {
        val centerX = tap.x * size.width
        val centerY = tap.y * size.height
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    HeatmapHot.copy(alpha = 0.4f),
                    HeatmapWarm.copy(alpha = 0.2f),
                    HeatmapCold.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = 100f
            ),
            center = Offset(centerX, centerY),
            radius = 100f
        )
    }
}

@Composable
private fun HintDisplay(level: Level, sensorState: SensorState) {
    val hints = buildList {
        for (anomaly in level.anomalies) {
            when (anomaly.visibilityCondition) {
                is VisibilityCondition.LowBrightness -> add("Try lowering brightness")
                is VisibilityCondition.HighBrightness -> add("Try increasing brightness")
                is VisibilityCondition.DuringRotation -> add("Try rotating your device")
                is VisibilityCondition.SpecificOrientation -> {
                    val condition = anomaly.visibilityCondition as VisibilityCondition.SpecificOrientation
                    if (condition.isLandscape) add("Try landscape mode")
                    else add("Try portrait mode")
                }
                is VisibilityCondition.AnimationPhase -> add("Watch for flickering")
                is VisibilityCondition.SystemUIHidden -> add("Try fullscreen mode")
                is VisibilityCondition.Always -> {}
            }
        }
    }.distinct().take(2)
    
    if (hints.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface.copy(alpha = 0.8f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Hints:",
                    style = MaterialTheme.typography.labelMedium,
                    color = WarningYellow
                )
                for (hint in hints) {
                    Text(
                        text = "• $hint",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
