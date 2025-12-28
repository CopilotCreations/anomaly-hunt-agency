package com.deadpixeldetective.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for game model classes.
 */
class GameModelsTest {
    
    @Test
    fun `Anomaly data class holds correct values`() {
        val anomaly = Anomaly(
            id = "test_123",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.7f,
            radius = 0.02f,
            visibilityCondition = VisibilityCondition.Always,
            animationPhase = 0.25f
        )
        
        assertEquals("test_123", anomaly.id)
        assertEquals(AnomalyType.PIXEL_OFFSET, anomaly.type)
        assertEquals(0.5f, anomaly.x, 0.001f)
        assertEquals(0.7f, anomaly.y, 0.001f)
        assertEquals(0.02f, anomaly.radius, 0.001f)
        assertEquals(VisibilityCondition.Always, anomaly.visibilityCondition)
        assertEquals(0.25f, anomaly.animationPhase, 0.001f)
    }
    
    @Test
    fun `AnomalyType enum contains all expected types`() {
        val types = AnomalyType.entries
        
        assertTrue(types.contains(AnomalyType.PIXEL_OFFSET))
        assertTrue(types.contains(AnomalyType.SUBTLE_GRADIENT))
        assertTrue(types.contains(AnomalyType.TEMPORAL_FLICKER))
        assertTrue(types.contains(AnomalyType.PIXEL_CLUSTER))
        assertTrue(types.contains(AnomalyType.COLOR_SHIFT))
        assertTrue(types.contains(AnomalyType.ROTATION_REVEAL))
        assertTrue(types.contains(AnomalyType.ORIENTATION_DEPENDENT))
        assertEquals(7, types.size)
    }
    
    @Test
    fun `Level data class holds correct values`() {
        val anomalies = listOf(
            Anomaly(
                id = "a1",
                type = AnomalyType.PIXEL_OFFSET,
                x = 0.5f,
                y = 0.5f,
                radius = 0.02f,
                visibilityCondition = VisibilityCondition.Always
            )
        )
        
        val level = Level(
            number = 5,
            anomalies = anomalies,
            maxAttempts = 4,
            difficulty = Difficulty.NORMAL
        )
        
        assertEquals(5, level.number)
        assertEquals(1, level.anomalies.size)
        assertEquals(4, level.maxAttempts)
        assertEquals(Difficulty.NORMAL, level.difficulty)
        assertNull(level.timeLimit)
    }
    
    @Test
    fun `Difficulty enum contains all expected levels`() {
        val difficulties = Difficulty.entries
        
        assertTrue(difficulties.contains(Difficulty.EASY))
        assertTrue(difficulties.contains(Difficulty.NORMAL))
        assertTrue(difficulties.contains(Difficulty.HARD))
        assertTrue(difficulties.contains(Difficulty.EXPERT))
        assertEquals(4, difficulties.size)
    }
    
    @Test
    fun `TapResult holds hit information correctly`() {
        val anomaly = Anomaly(
            id = "test",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val hitResult = TapResult(isHit = true, distance = 0.01f, anomalyFound = anomaly)
        val missResult = TapResult(isHit = false, distance = 0.5f, anomalyFound = null)
        
        assertTrue(hitResult.isHit)
        assertEquals(anomaly, hitResult.anomalyFound)
        
        assertFalse(missResult.isHit)
        assertNull(missResult.anomalyFound)
    }
    
    @Test
    fun `TapData records tap information correctly`() {
        val timestamp = System.currentTimeMillis()
        val tap = TapData(x = 0.3f, y = 0.7f, timestamp = timestamp, wasHit = true)
        
        assertEquals(0.3f, tap.x, 0.001f)
        assertEquals(0.7f, tap.y, 0.001f)
        assertEquals(timestamp, tap.timestamp)
        assertTrue(tap.wasHit)
    }
    
    @Test
    fun `GameState has correct default values`() {
        val state = GameState()
        
        assertNull(state.currentLevel)
        assertEquals(0, state.score)
        assertEquals(3, state.attemptsRemaining)
        assertTrue(state.foundAnomalies.isEmpty())
        assertTrue(state.tapHistory.isEmpty())
        assertFalse(state.isLevelComplete)
        assertFalse(state.isGameOver)
        assertFalse(state.showHeatmap)
    }
    
    @Test
    fun `SensorState has correct default values`() {
        val state = SensorState()
        
        assertEquals(0.5f, state.brightness, 0.001f)
        assertFalse(state.isRotating)
        assertEquals(0f, state.rotationAngle, 0.001f)
        assertFalse(state.isLandscape)
        assertTrue(state.isSystemUIVisible)
        assertEquals(0f, state.accelerometerX, 0.001f)
        assertEquals(0f, state.accelerometerY, 0.001f)
        assertEquals(0f, state.accelerometerZ, 0.001f)
    }
    
    @Test
    fun `UserPreferences has correct default values`() {
        val prefs = UserPreferences()
        
        assertFalse(prefs.highContrastMode)
        assertFalse(prefs.reducedMotion)
        assertTrue(prefs.hapticFeedback)
        assertTrue(prefs.soundEffects)
        assertFalse(prefs.showHints)
    }
    
    @Test
    fun `VisibilityCondition LowBrightness has default threshold`() {
        val condition = VisibilityCondition.LowBrightness()
        assertEquals(0.3f, condition.threshold, 0.001f)
    }
    
    @Test
    fun `VisibilityCondition HighBrightness has default threshold`() {
        val condition = VisibilityCondition.HighBrightness()
        assertEquals(0.7f, condition.threshold, 0.001f)
    }
    
    @Test
    fun `VisibilityCondition AnimationPhase stores phase range`() {
        val condition = VisibilityCondition.AnimationPhase(0.2f, 0.5f)
        assertEquals(0.2f, condition.minPhase, 0.001f)
        assertEquals(0.5f, condition.maxPhase, 0.001f)
    }
    
    @Test
    fun `VisibilityCondition SpecificOrientation stores orientation`() {
        val landscape = VisibilityCondition.SpecificOrientation(isLandscape = true)
        val portrait = VisibilityCondition.SpecificOrientation(isLandscape = false)
        
        assertTrue(landscape.isLandscape)
        assertFalse(portrait.isLandscape)
    }
    
    @Test
    fun `Anomaly copy creates correct copy with modified values`() {
        val original = Anomaly(
            id = "original",
            type = AnomalyType.PIXEL_OFFSET,
            x = 0.5f,
            y = 0.5f,
            radius = 0.02f,
            visibilityCondition = VisibilityCondition.Always
        )
        
        val copy = original.copy(x = 0.8f)
        
        assertEquals("original", copy.id)
        assertEquals(0.8f, copy.x, 0.001f)
        assertEquals(0.5f, copy.y, 0.001f)
    }
    
    @Test
    fun `GameState copy updates correctly`() {
        val original = GameState(score = 100)
        val updated = original.copy(score = 200, isLevelComplete = true)
        
        assertEquals(100, original.score)
        assertEquals(200, updated.score)
        assertTrue(updated.isLevelComplete)
        assertFalse(original.isLevelComplete)
    }
}
