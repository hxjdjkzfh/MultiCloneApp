package com.multiclone.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.preferences.UserPreferences
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the settings screen
 */
data class SettingsUiState(
    val darkThemeEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val operationMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel for the settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {
    
    // Private mutable state flow
    private val _uiState = MutableStateFlow(SettingsUiState())
    
    // Public immutable state flow
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        // Load preferences when ViewModel is created
        loadPreferences()
    }
    
    /**
     * Loads user preferences
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                val isDarkTheme = userPreferences.darkThemeEnabled.first()
                _uiState.update { it.copy(darkThemeEnabled = isDarkTheme) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading preferences")
                _uiState.update { 
                    it.copy(errorMessage = "Error loading preferences")
                }
            }
        }
    }
    
    /**
     * Sets the dark theme preference
     */
    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setDarkThemeEnabled(enabled)
                _uiState.update { it.copy(darkThemeEnabled = enabled) }
            } catch (e: Exception) {
                Timber.e(e, "Error setting dark theme preference")
                _uiState.update {
                    it.copy(errorMessage = "Error saving preference")
                }
            }
        }
    }
    
    /**
     * Clears all app data (clones and preferences)
     */
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true,
                    operationMessage = "Clearing all data..."
                )
            }
            
            try {
                // Clear all clones
                val clones = cloneRepository.clones.first()
                
                // Uninstall each clone from the virtual engine
                for (clone in clones) {
                    virtualAppEngine.removeClone(clone)
                }
                
                // Reset clone repository
                cloneRepository.loadClones() // This will reset to empty state
                
                // Reset preferences
                userPreferences.resetToDefaults()
                
                // Update UI state after preferences reset
                val isDarkTheme = userPreferences.darkThemeEnabled.first()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        darkThemeEnabled = isDarkTheme,
                        operationMessage = null
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error clearing all data")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        operationMessage = null,
                        errorMessage = "Error clearing data: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Dismisses the current error message
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}