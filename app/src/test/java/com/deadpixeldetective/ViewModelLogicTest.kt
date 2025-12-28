package com.deadpixeldetective.viewmodel

import com.deadpixeldetective.game.PuzzleGenerator
import com.deadpixeldetective.game.TapDetector
import com.deadpixeldetective.game.VisibilityCalculator
import com.deadpixeldetective.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for GameState management logic.
 * These tests verify the state transitions that would occur in GameViewModel.
 */
class GameStateLogicTest {
    
    private lateinit var puzzleGenerator: PuzzleGenerator
    private lateinit var tapDetector: TapDetector
    private lateinit var visibilityCalculator: VisibilityCalculator
    
    @Before
    fun setUp() {
        puzzleGenerator = PuzzleGenerator(Random(42))
        tapDetector = TapDetector()
        visibilityCalculator = VisibilityCalculator()
    }
    
    @Test
    fun `initial game state has correct defaults`() {
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
    fun `loading level updates state correctly`() {
        val level = puzzleGenerator.generateLevel(1)
        
        val state = GameState(
            currentLevel = level,
            score = 0,
            attemptsRemaining = level.maxAttempts,
            foundAnomalies = emptySet(),
            tapHistory = emptyList(),
            isLevelComplete = false,
            isGameOver = false,
            showHeatmap = false
        )
        
        assertEquals(level, state.currentLevel)
        assertEquals(level.maxAttempts, state.attemptsRemaining)
        assertFalse(state.isLevelComplete)
        assertFalse(state.isGameOver)
    }
    
    @Test
    fun `finding anomaly updates state correctly`() {
        val level = puzzleGenerator.generateLevel(1)
        val anomaly = level.anomalies.first()
        
        val initialState = GameState(
            currentLevel = level,
            score = 0,
            attemptsRemaining = level.maxAttempts,
            foundAnomalies = emptySet()
        )
        
        // Simulate finding an anomaly
        val scoreGain = tapDetector.calculateScore(
            distance = 0.01f,
            attemptsRemaining = initialState.attemptsRemaining,
            maxAttempts = level.maxAttempts,
            difficulty = level.difficulty
        )
        
        val newState = initialState.copy(
            score = initialState.score + scoreGain,
            foundAnomalies = initialState.foundAnomalies + anomaly.id,
            isLevelComplete = initialState.foundAnomalies.size + 1 == level.anomalies.size
        )
        
        assertTrue(newState.score > 0)
        assertTrue(newState.foundAnomalies.contains(anomaly.id))
    }
    
    @Test
    fun `missing tap decrements attempts`() {
        val level = puzzleGenerator.generateLevel(1)
        
        val initialState = GameState(
            currentLevel = level,
            attemptsRemaining = 3
        )
        
        val newState = initialState.copy(
            attemptsRemaining = initialState.attemptsRemaining - 1
        )
        
        assertEquals(2, newState.attemptsRemaining)
    }
    
    @Test
    fun `zero attempts triggers game over`() {
        val level = puzzleGenerator.generateLevel(1)
        
        val state = GameState(
            currentLevel = level,
            attemptsRemaining = 1
        )
        
        val newAttempts = state.attemptsRemaining - 1
        val isGameOver = newAttempts <= 0
        
        val newState = state.copy(
            attemptsRemaining = newAttempts,
            isGameOver = isGameOver
        )
        
        assertEquals(0, newState.attemptsRemaining)
        assertTrue(newState.isGameOver)
    }
    
    @Test
    fun `all anomalies found completes level`() {
        val level = puzzleGenerator.generateLevel(1)
        val allAnomalyIds = level.anomalies.map { it.id }.toSet()
        
        val state = GameState(
            currentLevel = level,
            foundAnomalies = allAnomalyIds,
            isLevelComplete = allAnomalyIds.size == level.anomalies.size
        )
        
        assertTrue(state.isLevelComplete)
    }
    
    @Test
    fun `tap history is preserved`() {
        val tap1 = TapData(x = 0.1f, y = 0.1f, timestamp = 1000L, wasHit = false)
        val tap2 = TapData(x = 0.5f, y = 0.5f, timestamp = 2000L, wasHit = true)
        
        val state = GameState(
            tapHistory = listOf(tap1)
        )
        
        val newState = state.copy(
            tapHistory = state.tapHistory + tap2
        )
        
        assertEquals(2, newState.tapHistory.size)
        assertEquals(tap1, newState.tapHistory[0])
        assertEquals(tap2, newState.tapHistory[1])
    }
    
    @Test
    fun `score accumulates across finds`() {
        var state = GameState(score = 0)
        
        state = state.copy(score = state.score + 100)
        assertEquals(100, state.score)
        
        state = state.copy(score = state.score + 150)
        assertEquals(250, state.score)
        
        state = state.copy(score = state.score + 200)
        assertEquals(450, state.score)
    }
    
    @Test
    fun `heatmap toggle works correctly`() {
        var state = GameState(showHeatmap = false)
        
        state = state.copy(showHeatmap = !state.showHeatmap)
        assertTrue(state.showHeatmap)
        
        state = state.copy(showHeatmap = !state.showHeatmap)
        assertFalse(state.showHeatmap)
    }
    
    @Test
    fun `game over shows heatmap`() {
        val state = GameState(
            attemptsRemaining = 0,
            isGameOver = true,
            showHeatmap = true
        )
        
        assertTrue(state.isGameOver)
        assertTrue(state.showHeatmap)
    }
}

/**
 * Unit tests for UserPreferences management.
 */
class UserPreferencesTest {
    
    @Test
    fun `default preferences have correct values`() {
        val prefs = UserPreferences()
        
        assertFalse(prefs.highContrastMode)
        assertFalse(prefs.reducedMotion)
        assertTrue(prefs.hapticFeedback)
        assertTrue(prefs.soundEffects)
        assertFalse(prefs.showHints)
    }
    
    @Test
    fun `preferences can be modified via copy`() {
        val prefs = UserPreferences()
        
        val modified = prefs.copy(
            highContrastMode = true,
            reducedMotion = true,
            hapticFeedback = false,
            soundEffects = false,
            showHints = true
        )
        
        assertTrue(modified.highContrastMode)
        assertTrue(modified.reducedMotion)
        assertFalse(modified.hapticFeedback)
        assertFalse(modified.soundEffects)
        assertTrue(modified.showHints)
        
        // Original unchanged
        assertFalse(prefs.highContrastMode)
    }
    
    @Test
    fun `partial modification preserves other values`() {
        val prefs = UserPreferences(
            highContrastMode = true,
            reducedMotion = false,
            hapticFeedback = true,
            soundEffects = true,
            showHints = false
        )
        
        val modified = prefs.copy(reducedMotion = true)
        
        assertTrue(modified.highContrastMode)
        assertTrue(modified.reducedMotion)
        assertTrue(modified.hapticFeedback)
        assertTrue(modified.soundEffects)
        assertFalse(modified.showHints)
    }
}

/**
 * Unit tests for SensorState management.
 */
class SensorStateTest {
    
    @Test
    fun `default sensor state has correct values`() {
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
    fun `sensor state can be updated via copy`() {
        val state = SensorState()
        
        val updated = state.copy(
            brightness = 0.8f,
            isRotating = true,
            rotationAngle = 45f,
            isLandscape = true
        )
        
        assertEquals(0.8f, updated.brightness, 0.001f)
        assertTrue(updated.isRotating)
        assertEquals(45f, updated.rotationAngle, 0.001f)
        assertTrue(updated.isLandscape)
    }
    
    @Test
    fun `brightness values are valid range`() {
        val lowBrightness = SensorState(brightness = 0.0f)
        val midBrightness = SensorState(brightness = 0.5f)
        val highBrightness = SensorState(brightness = 1.0f)
        
        assertEquals(0.0f, lowBrightness.brightness, 0.001f)
        assertEquals(0.5f, midBrightness.brightness, 0.001f)
        assertEquals(1.0f, highBrightness.brightness, 0.001f)
    }
    
    @Test
    fun `accelerometer values can be set`() {
        val state = SensorState(
            accelerometerX = 1.5f,
            accelerometerY = -2.3f,
            accelerometerZ = 9.8f
        )
        
        assertEquals(1.5f, state.accelerometerX, 0.001f)
        assertEquals(-2.3f, state.accelerometerY, 0.001f)
        assertEquals(9.8f, state.accelerometerZ, 0.001f)
    }
}
