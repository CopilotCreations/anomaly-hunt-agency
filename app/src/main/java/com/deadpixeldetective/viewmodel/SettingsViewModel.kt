package com.deadpixeldetective.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deadpixeldetective.data.PreferencesRepository
import com.deadpixeldetective.model.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for settings screen.
 * Manages user preferences.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesRepository = PreferencesRepository(application)
    
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())
    
    fun setHighContrastMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHighContrastMode(enabled)
        }
    }
    
    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateReducedMotion(enabled)
        }
    }
    
    fun setHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHapticFeedback(enabled)
        }
    }
    
    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateSoundEffects(enabled)
        }
    }
    
    fun setShowHints(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateShowHints(enabled)
        }
    }
}
