package com.deadpixeldetective.game

import com.deadpixeldetective.model.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

/**
 * Additional comprehensive tests for puzzle generation.
 */
class PuzzleGeneratorComprehensiveTest {
    
    @Test
    fun `generateLevel produces consistent results with same seed`() {
        val generator1 = PuzzleGenerator(Random(12345))
        val generator2 = PuzzleGenerator(Random(12345))
        
        val level1 = generator1.generateLevel(5)
        val level2 = generator2.generateLevel(5)
        
        assertEquals(level1.number, level2.number)
        assertEquals(level1.maxAttempts, level2.maxAttempts)
        assertEquals(level1.difficulty, level2.difficulty)
        assertEquals(level1.anomalies.size, level2.anomalies.size)
        
        // Check that anomalies are identical
        for (i in level1.anomalies.indices) {
            assertEquals(level1.anomalies[i].x, level2.anomalies[i].x, 0.0001f)
            assertEquals(level1.anomalies[i].y, level2.anomalies[i].y, 0.0001f)
            assertEquals(level1.anomalies[i].type, level2.anomalies[i].type)
        }
    }
    
    @Test
    fun `all anomaly positions are within valid bounds`() {
        val generator = PuzzleGenerator()
        
        for (levelNum in 1..20) {
            val level = generator.generateLevel(levelNum)
            
            for (anomaly in level.anomalies) {
                assertTrue("x=${anomaly.x} should be >= 0.1", anomaly.x >= 0.1f)
                assertTrue("x=${anomaly.x} should be <= 0.9", anomaly.x <= 0.9f)
                assertTrue("y=${anomaly.y} should be >= 0.1", anomaly.y >= 0.1f)
                assertTrue("y=${anomaly.y} should be <= 0.9", anomaly.y <= 0.9f)
            }
        }
    }
    
    @Test
    fun `all anomaly radii are positive`() {
        val generator = PuzzleGenerator()
        
        for (levelNum in 1..20) {
            val level = generator.generateLevel(levelNum)
            
            for (anomaly in level.anomalies) {
                assertTrue("radius should be positive", anomaly.radius > 0)
            }
        }
    }
    
    @Test
    fun `level difficulty increases over time`() {
        val generator = PuzzleGenerator()
        
        val easyLevel = generator.generateLevel(1)
        val normalLevel = generator.generateLevel(5)
        val hardLevel = generator.generateLevel(10)
        val expertLevel = generator.generateLevel(15)
        
        assertEquals(Difficulty.EASY, easyLevel.difficulty)
        assertEquals(Difficulty.NORMAL, normalLevel.difficulty)
        assertEquals(Difficulty.HARD, hardLevel.difficulty)
        assertEquals(Difficulty.EXPERT, expertLevel.difficulty)
    }
    
    @Test
    fun `max attempts decrease with difficulty`() {
        val generator = PuzzleGenerator()
        
        val easyAttempts = generator.calculateMaxAttempts(1, Difficulty.EASY)
        val normalAttempts = generator.calculateMaxAttempts(5, Difficulty.NORMAL)
        val hardAttempts = generator.calculateMaxAttempts(10, Difficulty.HARD)
        val expertAttempts = generator.calculateMaxAttempts(15, Difficulty.EXPERT)
        
        assertTrue(easyAttempts > normalAttempts)
        assertTrue(normalAttempts > hardAttempts)
        assertTrue(hardAttempts >= expertAttempts)
    }
    
    @Test
    fun `anomaly animation phases are between 0 and 1`() {
        val generator = PuzzleGenerator()
        
        for (levelNum in 1..10) {
            val level = generator.generateLevel(levelNum)
            
            for (anomaly in level.anomalies) {
                assertTrue("animationPhase should be >= 0", anomaly.animationPhase >= 0f)
                assertTrue("animationPhase should be < 1", anomaly.animationPhase < 1f)
            }
        }
    }
    
    @Test
    fun `EASY difficulty only uses simple anomaly types`() {
        val generator = PuzzleGenerator(Random(42))
        
        val simpleTypes = setOf(
            AnomalyType.PIXEL_OFFSET,
            AnomalyType.PIXEL_CLUSTER,
            AnomalyType.SUBTLE_GRADIENT
        )
        
        // Generate many levels to statistically verify
        for (i in 1..50) {
            val type = generator.selectAnomalyType(1, Difficulty.EASY)
            assertTrue(
                "EASY difficulty should use simple types, got $type",
                type in simpleTypes
            )
        }
    }
    
    @Test
    fun `EXPERT difficulty can use all anomaly types`() {
        val generator = PuzzleGenerator(Random(42))
        
        val foundTypes = mutableSetOf<AnomalyType>()
        
        // Generate many samples to ensure variety
        for (i in 1..1000) {
            val type = generator.selectAnomalyType(15, Difficulty.EXPERT)
            foundTypes.add(type)
        }
        
        // Should eventually find all types
        assertEquals(
            "EXPERT should be able to produce all anomaly types",
            AnomalyType.entries.toSet(),
            foundTypes
        )
    }
}

/**
 * Additional comprehensive tests for tap detection.
 */
class TapDetectorComprehensiveTest {
    
    private val detector = TapDetector()
    
    @Test
    fun `hit detection uses correct tolerance multiplier`() {
        val anomaly = Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        // Tap exactly at tolerance boundary (radius * 2)
        val boundaryResult = detector.checkTap(0.5f, 0.5f + 0.039f, listOf(anomaly), emptySet())
        assertTrue("Should hit at tolerance boundary", boundaryResult.isHit)
        
        // Tap just outside tolerance
        val outsideResult = detector.checkTap(0.5f, 0.5f + 0.05f, listOf(anomaly), emptySet())
        assertFalse("Should miss outside tolerance", outsideResult.isHit)
    }
    
    @Test
    fun `empty anomaly list returns miss`() {
        val result = detector.checkTap(0.5f, 0.5f, emptyList(), emptySet())
        
        assertFalse(result.isHit)
        assertNull(result.anomalyFound)
        assertEquals(Float.MAX_VALUE, result.distance, 0.001f)
    }
    
    @Test
    fun `all anomalies found returns miss`() {
        val anomaly = Anomaly(
            id = "only_one",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.05f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val result = detector.checkTap(0.5f, 0.5f, listOf(anomaly), setOf("only_one"))
        
        assertFalse(result.isHit)
        assertNull(result.anomalyFound)
    }
    
    @Test
    fun `score calculation handles edge cases`() {
        // Zero distance should give maximum accuracy bonus
        val perfectScore = detector.calculateScore(0f, 5, 5, Difficulty.NORMAL)
        
        // Large distance should still give some points
        val poorScore = detector.calculateScore(0.1f, 1, 5, Difficulty.NORMAL)
        
        assertTrue(perfectScore > poorScore)
        assertTrue(poorScore > 0)
    }
    
    @Test
    fun `distance calculation is symmetric`() {
        val d1 = detector.calculateDistance(0f, 0f, 3f, 4f)
        val d2 = detector.calculateDistance(3f, 4f, 0f, 0f)
        
        assertEquals(d1, d2, 0.0001f)
    }
    
    @Test
    fun `calculateScore returns positive for all valid inputs`() {
        for (difficulty in Difficulty.entries) {
            for (attempts in 1..5) {
                for (distance in listOf(0f, 0.05f, 0.1f)) {
                    val score = detector.calculateScore(distance, attempts, 5, difficulty)
                    assertTrue("Score should be positive: got $score", score > 0)
                }
            }
        }
    }
}

/**
 * Additional comprehensive tests for visibility calculation.
 */
class VisibilityCalculatorComprehensiveTest {
    
    private val calculator = VisibilityCalculator()
    
    @Test
    fun `visibility values are always between 0 and 1`() {
        val conditions = listOf(
            VisibilityCondition.Always,
            VisibilityCondition.LowBrightness(0.3f),
            VisibilityCondition.HighBrightness(0.7f),
            VisibilityCondition.DuringRotation,
            VisibilityCondition.SpecificOrientation(true),
            VisibilityCondition.SpecificOrientation(false),
            VisibilityCondition.AnimationPhase(0.2f, 0.4f),
            VisibilityCondition.SystemUIHidden
        )
        
        val sensorStates = listOf(
            SensorState(brightness = 0f),
            SensorState(brightness = 0.5f),
            SensorState(brightness = 1f),
            SensorState(isRotating = true),
            SensorState(isRotating = false),
            SensorState(isLandscape = true),
            SensorState(isLandscape = false),
            SensorState(isSystemUIVisible = true),
            SensorState(isSystemUIVisible = false)
        )
        
        for (condition in conditions) {
            for (state in sensorStates) {
                for (phase in listOf(0f, 0.3f, 0.5f, 0.7f, 1f)) {
                    val anomaly = createAnomaly(condition)
                    val visibility = calculator.calculateVisibility(anomaly, state, phase)
                    
                    assertTrue(
                        "Visibility should be >= 0, got $visibility for $condition",
                        visibility >= 0f
                    )
                    assertTrue(
                        "Visibility should be <= 1, got $visibility for $condition",
                        visibility <= 1f
                    )
                }
            }
        }
    }
    
    @Test
    fun `brightness conditions scale smoothly`() {
        val lowBrightnessAnomaly = createAnomaly(VisibilityCondition.LowBrightness(0.3f))
        
        var previousVisibility = 1.0f
        for (brightness in listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f)) {
            val state = SensorState(brightness = brightness)
            val visibility = calculator.calculateVisibility(lowBrightnessAnomaly, state, 0f)
            
            // Visibility should decrease or stay same as brightness increases
            assertTrue(
                "Visibility should not increase with brightness",
                visibility <= previousVisibility + 0.01f // Small tolerance for edge cases
            )
            previousVisibility = visibility
        }
    }
    
    @Test
    fun `animation phase wraps correctly`() {
        val anomaly = createAnomaly(VisibilityCondition.AnimationPhase(0.2f, 0.4f))
        val state = SensorState()
        
        // Test phase just at boundaries
        val atStart = calculator.calculateVisibility(anomaly, state, 0.2f)
        val inMiddle = calculator.calculateVisibility(anomaly, state, 0.3f)
        val atEnd = calculator.calculateVisibility(anomaly, state, 0.4f)
        val outside = calculator.calculateVisibility(anomaly, state, 0.6f)
        
        assertEquals(1.0f, atStart, 0.001f)
        assertEquals(1.0f, inMiddle, 0.001f)
        assertEquals(1.0f, atEnd, 0.001f)
        assertEquals(0.1f, outside, 0.001f)
    }
    
    private fun createAnomaly(condition: VisibilityCondition): Anomaly {
        return Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = condition
        )
    }
}
