package com.deadpixeldetective.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.deadpixeldetective.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages persistent user preferences using DataStore.
 */
class PreferencesRepository(private val context: Context) {
    
    private object PreferencesKeys {
        val HIGH_CONTRAST_MODE = booleanPreferencesKey("high_contrast_mode")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val SHOW_HINTS = booleanPreferencesKey("show_hints")
        val HIGH_SCORE = intPreferencesKey("high_score")
        val HIGHEST_LEVEL = intPreferencesKey("highest_level")
        val TOTAL_ANOMALIES_FOUND = intPreferencesKey("total_anomalies_found")
    }
    
    /**
     * Flow of user preferences.
     */
    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                highContrastMode = preferences[PreferencesKeys.HIGH_CONTRAST_MODE] ?: false,
                reducedMotion = preferences[PreferencesKeys.REDUCED_MOTION] ?: false,
                hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
                soundEffects = preferences[PreferencesKeys.SOUND_EFFECTS] ?: true,
                showHints = preferences[PreferencesKeys.SHOW_HINTS] ?: false
            )
        }
    
    /**
     * Flow of high score.
     */
    val highScore: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.HIGH_SCORE] ?: 0 }
    
    /**
     * Flow of highest level reached.
     */
    val highestLevel: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.HIGHEST_LEVEL] ?: 1 }
    
    /**
     * Flow of total anomalies found.
     */
    val totalAnomaliesFound: Flow<Int> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PreferencesKeys.TOTAL_ANOMALIES_FOUND] ?: 0 }
    
    suspend fun updateHighContrastMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HIGH_CONTRAST_MODE] = enabled }
    }
    
    suspend fun updateReducedMotion(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.REDUCED_MOTION] = enabled }
    }
    
    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }
    
    suspend fun updateSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_EFFECTS] = enabled }
    }
    
    suspend fun updateShowHints(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_HINTS] = enabled }
    }
    
    suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHigh = preferences[PreferencesKeys.HIGH_SCORE] ?: 0
            if (score > currentHigh) {
                preferences[PreferencesKeys.HIGH_SCORE] = score
            }
        }
    }
    
    suspend fun updateHighestLevel(level: Int) {
        context.dataStore.edit { preferences ->
            val currentHighest = preferences[PreferencesKeys.HIGHEST_LEVEL] ?: 1
            if (level > currentHighest) {
                preferences[PreferencesKeys.HIGHEST_LEVEL] = level
            }
        }
    }
    
    suspend fun incrementAnomaliesFound(count: Int = 1) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TOTAL_ANOMALIES_FOUND] ?: 0
            preferences[PreferencesKeys.TOTAL_ANOMALIES_FOUND] = current + count
        }
    }
}
