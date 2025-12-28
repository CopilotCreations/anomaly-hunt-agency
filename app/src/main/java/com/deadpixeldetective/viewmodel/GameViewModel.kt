package com.deadpixeldetective.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deadpixeldetective.data.PreferencesRepository
import com.deadpixeldetective.game.PuzzleGenerator
import com.deadpixeldetective.game.TapDetector
import com.deadpixeldetective.game.VisibilityCalculator
import com.deadpixeldetective.model.*
import com.deadpixeldetective.sensor.SensorManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the main game screen.
 * Manages game state, sensor integration, and user interactions.
 */
class GameViewModel(application: Application) : AndroidViewModel(application) {
    
    private val puzzleGenerator = PuzzleGenerator()
    private val tapDetector = TapDetector()
    private val visibilityCalculator = VisibilityCalculator()
    private val sensorManager = SensorManager(application)
    private val preferencesRepository = PreferencesRepository(application)
    
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(VibratorManager::class.java)
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Vibrator::class.java)
    }
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _sensorState = MutableStateFlow(SensorState())
    val sensorState: StateFlow<SensorState> = _sensorState.asStateFlow()
    
    private val _animationProgress = MutableStateFlow(0f)
    val animationProgress: StateFlow<Float> = _animationProgress.asStateFlow()
    
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())
    
    val highScore: StateFlow<Int> = preferencesRepository.highScore
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    
    val highestLevel: StateFlow<Int> = preferencesRepository.highestLevel
        .stateIn(viewModelScope, SharingStarted.Eagerly, 1)
    
    private var currentLevelNumber = 1
    private var isSensorCollectionActive = false
    
    init {
        startSensorCollection()
    }
    
    private fun startSensorCollection() {
        if (isSensorCollectionActive) return
        isSensorCollectionActive = true
        
        viewModelScope.launch {
            sensorManager.sensorStateFlow().collect { state ->
                _sensorState.value = state
            }
        }
    }
    
    /**
     * Starts a new game from level 1.
     */
    fun startNewGame() {
        currentLevelNumber = 1
        _gameState.value = GameState()
        loadLevel(currentLevelNumber)
    }
    
    /**
     * Loads a specific level.
     */
    fun loadLevel(levelNumber: Int) {
        currentLevelNumber = levelNumber
        val level = puzzleGenerator.generateLevel(levelNumber)
        
        _gameState.value = GameState(
            currentLevel = level,
            score = _gameState.value.score,
            attemptsRemaining = level.maxAttempts,
            foundAnomalies = emptySet(),
            tapHistory = emptyList(),
            isLevelComplete = false,
            isGameOver = false,
            showHeatmap = false
        )
        
        viewModelScope.launch {
            preferencesRepository.updateHighestLevel(levelNumber)
        }
    }
    
    /**
     * Handles a tap on the game canvas.
     */
    fun onTap(normalizedX: Float, normalizedY: Float) {
        val state = _gameState.value
        val level = state.currentLevel ?: return
        
        if (state.isLevelComplete || state.isGameOver) return
        
        val tapData = TapData(
            x = normalizedX,
            y = normalizedY,
            timestamp = System.currentTimeMillis(),
            wasHit = false
        )
        
        val result = tapDetector.checkTap(
            tapX = normalizedX,
            tapY = normalizedY,
            anomalies = level.anomalies,
            foundAnomalies = state.foundAnomalies
        )
        
        if (result.isHit && result.anomalyFound != null) {
            handleHit(result, tapData)
        } else {
            handleMiss(tapData)
        }
    }
    
    private fun handleHit(result: TapResult, tapData: TapData) {
        val state = _gameState.value
        val level = state.currentLevel ?: return
        val anomaly = result.anomalyFound ?: return
        
        val scoreGain = tapDetector.calculateScore(
            distance = result.distance,
            attemptsRemaining = state.attemptsRemaining,
            maxAttempts = level.maxAttempts,
            difficulty = level.difficulty
        )
        
        val newFoundAnomalies = state.foundAnomalies + anomaly.id
        val isComplete = newFoundAnomalies.size == level.anomalies.size
        
        _gameState.value = state.copy(
            score = state.score + scoreGain,
            foundAnomalies = newFoundAnomalies,
            tapHistory = state.tapHistory + tapData.copy(wasHit = true),
            isLevelComplete = isComplete
        )
        
        viewModelScope.launch {
            preferencesRepository.updateHighScore(state.score + scoreGain)
            preferencesRepository.incrementAnomaliesFound()
        }
        
        if (userPreferences.value.hapticFeedback) {
            triggerHapticFeedback(success = true)
        }
    }
    
    private fun handleMiss(tapData: TapData) {
        val state = _gameState.value
        val newAttempts = state.attemptsRemaining - 1
        val isGameOver = newAttempts <= 0
        
        _gameState.value = state.copy(
            attemptsRemaining = newAttempts,
            tapHistory = state.tapHistory + tapData,
            isGameOver = isGameOver,
            showHeatmap = isGameOver
        )
        
        if (userPreferences.value.hapticFeedback) {
            triggerHapticFeedback(success = false)
        }
    }
    
    private fun triggerHapticFeedback(success: Boolean) {
        val effect = if (success) {
            VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE / 2)
        }
        vibrator.vibrate(effect)
    }
    
    /**
     * Advances to the next level.
     */
    fun nextLevel() {
        loadLevel(currentLevelNumber + 1)
    }
    
    /**
     * Retries the current level.
     */
    fun retryLevel() {
        _gameState.value = _gameState.value.copy(score = 0)
        loadLevel(currentLevelNumber)
    }
    
    /**
     * Toggles heatmap visibility.
     */
    fun toggleHeatmap() {
        _gameState.value = _gameState.value.copy(
            showHeatmap = !_gameState.value.showHeatmap
        )
    }
    
    /**
     * Updates animation progress for time-based anomalies.
     */
    fun updateAnimationProgress(progress: Float) {
        _animationProgress.value = progress
    }
    
    /**
     * Calculates visibility for a specific anomaly.
     */
    fun getAnomalyVisibility(anomaly: Anomaly): Float {
        return visibilityCalculator.calculateVisibility(
            anomaly = anomaly,
            sensorState = _sensorState.value,
            animationProgress = _animationProgress.value
        )
    }
    
    /**
     * Gets current level number.
     */
    fun getCurrentLevelNumber(): Int = currentLevelNumber
}
