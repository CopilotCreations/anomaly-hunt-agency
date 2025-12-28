package com.deadpixeldetective.model

import kotlin.random.Random

/**
 * Represents a single visual anomaly on the game canvas.
 * Anomalies can have different types and visibility conditions.
 */
data class Anomaly(
    val id: String,
    val type: AnomalyType,
    val x: Float,
    val y: Float,
    val radius: Float,
    val visibilityCondition: VisibilityCondition,
    val animationPhase: Float = Random.nextFloat()
)

/**
 * Types of visual anomalies that can appear in the game.
 */
enum class AnomalyType {
    /** A single pixel offset from its expected position */
    PIXEL_OFFSET,
    
    /** A barely visible gradient that shifts subtly */
    SUBTLE_GRADIENT,
    
    /** A pixel that flickers at specific intervals */
    TEMPORAL_FLICKER,
    
    /** A small cluster of misaligned pixels */
    PIXEL_CLUSTER,
    
    /** Color shift visible under certain brightness */
    COLOR_SHIFT,
    
    /** Pattern that emerges during rotation */
    ROTATION_REVEAL,
    
    /** Anomaly that appears only in specific orientation */
    ORIENTATION_DEPENDENT
}

/**
 * Conditions under which an anomaly becomes visible or more noticeable.
 */
sealed class VisibilityCondition {
    /** Always visible (base difficulty) */
    data object Always : VisibilityCondition()
    
    /** More visible at low brightness levels */
    data class LowBrightness(val threshold: Float = 0.3f) : VisibilityCondition()
    
    /** More visible at high brightness levels */
    data class HighBrightness(val threshold: Float = 0.7f) : VisibilityCondition()
    
    /** Visible during device rotation */
    data object DuringRotation : VisibilityCondition()
    
    /** Visible in specific orientation */
    data class SpecificOrientation(val isLandscape: Boolean) : VisibilityCondition()
    
    /** Visible at specific animation phase */
    data class AnimationPhase(val minPhase: Float, val maxPhase: Float) : VisibilityCondition()
    
    /** Visible when system UI is hidden */
    data object SystemUIHidden : VisibilityCondition()
}

/**
 * Represents a game level with its anomalies and configuration.
 */
data class Level(
    val number: Int,
    val anomalies: List<Anomaly>,
    val maxAttempts: Int,
    val timeLimit: Long? = null,
    val difficulty: Difficulty = Difficulty.NORMAL
)

/**
 * Difficulty levels affecting anomaly visibility and player assistance.
 */
enum class Difficulty {
    EASY,
    NORMAL,
    HARD,
    EXPERT
}

/**
 * Result of a player's tap attempt.
 */
data class TapResult(
    val isHit: Boolean,
    val distance: Float,
    val anomalyFound: Anomaly? = null
)

/**
 * Accumulated tap data for generating heatmaps.
 */
data class TapData(
    val x: Float,
    val y: Float,
    val timestamp: Long,
    val wasHit: Boolean
)

/**
 * Current game state.
 */
data class GameState(
    val currentLevel: Level? = null,
    val score: Int = 0,
    val attemptsRemaining: Int = 3,
    val foundAnomalies: Set<String> = emptySet(),
    val tapHistory: List<TapData> = emptyList(),
    val isLevelComplete: Boolean = false,
    val isGameOver: Boolean = false,
    val showHeatmap: Boolean = false
)

/**
 * Sensor data collected from device sensors.
 */
data class SensorState(
    val brightness: Float = 0.5f,
    val isRotating: Boolean = false,
    val rotationAngle: Float = 0f,
    val isLandscape: Boolean = false,
    val isSystemUIVisible: Boolean = true,
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 0f
)

/**
 * User preferences for accessibility and gameplay.
 */
data class UserPreferences(
    val highContrastMode: Boolean = false,
    val reducedMotion: Boolean = false,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = true,
    val showHints: Boolean = false
)
