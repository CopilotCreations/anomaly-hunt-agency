package com.deadpixeldetective.game

import com.deadpixeldetective.model.*
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Generates puzzles (levels) with anomalies based on difficulty and level number.
 * This is a pure function class with no side effects, making it easily testable.
 */
class PuzzleGenerator(
    private val random: Random = Random.Default
) {
    
    companion object {
        const val MIN_ANOMALY_MARGIN = 0.1f
        const val MAX_ANOMALY_MARGIN = 0.9f
        const val BASE_RADIUS = 0.02f
        const val BASE_ATTEMPTS = 5
    }
    
    /**
     * Generates a level with appropriate difficulty based on level number.
     */
    fun generateLevel(levelNumber: Int): Level {
        val difficulty = calculateDifficulty(levelNumber)
        val anomalyCount = calculateAnomalyCount(levelNumber, difficulty)
        val maxAttempts = calculateMaxAttempts(levelNumber, difficulty)
        
        val anomalies = (1..anomalyCount).map { index ->
            generateAnomaly(
                id = "level${levelNumber}_anomaly$index",
                levelNumber = levelNumber,
                difficulty = difficulty
            )
        }
        
        return Level(
            number = levelNumber,
            anomalies = anomalies,
            maxAttempts = maxAttempts,
            difficulty = difficulty
        )
    }
    
    /**
     * Calculates difficulty based on level number.
     */
    fun calculateDifficulty(levelNumber: Int): Difficulty {
        return when {
            levelNumber <= 3 -> Difficulty.EASY
            levelNumber <= 7 -> Difficulty.NORMAL
            levelNumber <= 12 -> Difficulty.HARD
            else -> Difficulty.EXPERT
        }
    }
    
    /**
     * Calculates number of anomalies for a level.
     */
    fun calculateAnomalyCount(levelNumber: Int, difficulty: Difficulty): Int {
        val base = when (difficulty) {
            Difficulty.EASY -> 1
            Difficulty.NORMAL -> 1
            Difficulty.HARD -> 2
            Difficulty.EXPERT -> 2
        }
        return base + (levelNumber / 5).coerceAtMost(2)
    }
    
    /**
     * Calculates maximum attempts allowed for a level.
     */
    fun calculateMaxAttempts(levelNumber: Int, difficulty: Difficulty): Int {
        val base = when (difficulty) {
            Difficulty.EASY -> 5
            Difficulty.NORMAL -> 4
            Difficulty.HARD -> 3
            Difficulty.EXPERT -> 2
        }
        return base
    }
    
    /**
     * Generates a single anomaly with appropriate type and visibility condition.
     */
    fun generateAnomaly(id: String, levelNumber: Int, difficulty: Difficulty): Anomaly {
        val type = selectAnomalyType(levelNumber, difficulty)
        val visibilityCondition = selectVisibilityCondition(levelNumber, difficulty, type)
        val radius = calculateRadius(difficulty)
        
        return Anomaly(
            id = id,
            type = type,
            x = random.nextFloat() * (MAX_ANOMALY_MARGIN - MIN_ANOMALY_MARGIN) + MIN_ANOMALY_MARGIN,
            y = random.nextFloat() * (MAX_ANOMALY_MARGIN - MIN_ANOMALY_MARGIN) + MIN_ANOMALY_MARGIN,
            radius = radius,
            visibilityCondition = visibilityCondition,
            animationPhase = random.nextFloat()
        )
    }
    
    /**
     * Selects anomaly type based on level and difficulty.
     */
    fun selectAnomalyType(levelNumber: Int, difficulty: Difficulty): AnomalyType {
        val availableTypes = when (difficulty) {
            Difficulty.EASY -> listOf(
                AnomalyType.PIXEL_OFFSET,
                AnomalyType.PIXEL_CLUSTER,
                AnomalyType.SUBTLE_GRADIENT
            )
            Difficulty.NORMAL -> listOf(
                AnomalyType.PIXEL_OFFSET,
                AnomalyType.PIXEL_CLUSTER,
                AnomalyType.SUBTLE_GRADIENT,
                AnomalyType.COLOR_SHIFT
            )
            Difficulty.HARD -> listOf(
                AnomalyType.TEMPORAL_FLICKER,
                AnomalyType.COLOR_SHIFT,
                AnomalyType.ROTATION_REVEAL,
                AnomalyType.SUBTLE_GRADIENT
            )
            Difficulty.EXPERT -> AnomalyType.entries
        }
        return availableTypes[random.nextInt(availableTypes.size)]
    }
    
    /**
     * Selects visibility condition based on level, difficulty, and anomaly type.
     */
    fun selectVisibilityCondition(
        levelNumber: Int,
        difficulty: Difficulty,
        type: AnomalyType
    ): VisibilityCondition {
        // Type-specific conditions
        val typeBasedCondition = when (type) {
            AnomalyType.ROTATION_REVEAL -> VisibilityCondition.DuringRotation
            AnomalyType.ORIENTATION_DEPENDENT -> VisibilityCondition.SpecificOrientation(random.nextBoolean())
            AnomalyType.TEMPORAL_FLICKER -> VisibilityCondition.AnimationPhase(
                minPhase = random.nextFloat() * 0.3f,
                maxPhase = random.nextFloat() * 0.3f + 0.3f
            )
            else -> null
        }
        
        if (typeBasedCondition != null) return typeBasedCondition
        
        // Difficulty-based conditions
        return when (difficulty) {
            Difficulty.EASY -> VisibilityCondition.Always
            Difficulty.NORMAL -> if (random.nextFloat() > 0.5f) {
                VisibilityCondition.Always
            } else {
                listOf(
                    VisibilityCondition.LowBrightness(),
                    VisibilityCondition.HighBrightness()
                ).random(random)
            }
            Difficulty.HARD, Difficulty.EXPERT -> listOf(
                VisibilityCondition.LowBrightness(0.2f),
                VisibilityCondition.HighBrightness(0.8f),
                VisibilityCondition.DuringRotation,
                VisibilityCondition.SpecificOrientation(random.nextBoolean()),
                VisibilityCondition.AnimationPhase(0.2f, 0.4f)
            ).random(random)
        }
    }
    
    /**
     * Calculates anomaly radius based on difficulty (smaller = harder to find).
     */
    fun calculateRadius(difficulty: Difficulty): Float {
        return when (difficulty) {
            Difficulty.EASY -> BASE_RADIUS * 2.0f
            Difficulty.NORMAL -> BASE_RADIUS * 1.5f
            Difficulty.HARD -> BASE_RADIUS * 1.2f
            Difficulty.EXPERT -> BASE_RADIUS
        }
    }
}

/**
 * Handles tap detection and scoring logic.
 */
class TapDetector {
    
    companion object {
        const val HIT_TOLERANCE_MULTIPLIER = 2.0f
    }
    
    /**
     * Checks if a tap hit any anomaly.
     */
    fun checkTap(
        tapX: Float,
        tapY: Float,
        anomalies: List<Anomaly>,
        foundAnomalies: Set<String>
    ): TapResult {
        var closestDistance = Float.MAX_VALUE
        var hitAnomaly: Anomaly? = null
        
        for (anomaly in anomalies) {
            if (anomaly.id in foundAnomalies) continue
            
            val distance = calculateDistance(tapX, tapY, anomaly.x, anomaly.y)
            val hitRadius = anomaly.radius * HIT_TOLERANCE_MULTIPLIER
            
            if (distance <= hitRadius && distance < closestDistance) {
                closestDistance = distance
                hitAnomaly = anomaly
            }
        }
        
        return TapResult(
            isHit = hitAnomaly != null,
            distance = closestDistance,
            anomalyFound = hitAnomaly
        )
    }
    
    /**
     * Calculates Euclidean distance between two points.
     */
    fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }
    
    /**
     * Calculates score for a successful find.
     */
    fun calculateScore(
        distance: Float,
        attemptsRemaining: Int,
        maxAttempts: Int,
        difficulty: Difficulty
    ): Int {
        val baseScore = when (difficulty) {
            Difficulty.EASY -> 100
            Difficulty.NORMAL -> 200
            Difficulty.HARD -> 350
            Difficulty.EXPERT -> 500
        }
        
        // Accuracy bonus (closer = more points)
        val accuracyMultiplier = (1.0f - distance.coerceIn(0f, 0.1f) * 5f).coerceAtLeast(0.5f)
        
        // Attempt efficiency bonus
        val attemptBonus = (attemptsRemaining.toFloat() / maxAttempts) * 0.5f + 0.5f
        
        return (baseScore * accuracyMultiplier * attemptBonus).toInt()
    }
}

/**
 * Determines anomaly visibility based on current sensor state.
 */
class VisibilityCalculator {
    
    /**
     * Calculates how visible an anomaly should be (0.0 = invisible, 1.0 = fully visible).
     */
    fun calculateVisibility(
        anomaly: Anomaly,
        sensorState: SensorState,
        animationProgress: Float
    ): Float {
        return when (val condition = anomaly.visibilityCondition) {
            is VisibilityCondition.Always -> 1.0f
            
            is VisibilityCondition.LowBrightness -> {
                if (sensorState.brightness <= condition.threshold) 1.0f
                else (condition.threshold / sensorState.brightness).coerceIn(0.3f, 1.0f)
            }
            
            is VisibilityCondition.HighBrightness -> {
                if (sensorState.brightness >= condition.threshold) 1.0f
                else (sensorState.brightness / condition.threshold).coerceIn(0.3f, 1.0f)
            }
            
            is VisibilityCondition.DuringRotation -> {
                if (sensorState.isRotating) 1.0f else 0.2f
            }
            
            is VisibilityCondition.SpecificOrientation -> {
                if (sensorState.isLandscape == condition.isLandscape) 1.0f else 0.15f
            }
            
            is VisibilityCondition.AnimationPhase -> {
                val normalizedPhase = animationProgress % 1.0f
                if (normalizedPhase in condition.minPhase..condition.maxPhase) 1.0f
                else 0.1f
            }
            
            is VisibilityCondition.SystemUIHidden -> {
                if (!sensorState.isSystemUIVisible) 1.0f else 0.25f
            }
        }
    }
}
