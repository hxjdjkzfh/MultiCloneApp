package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    
    // UI state for the app selection screen
    private val _uiState = MutableStateFlow(AppSelectionUiState())
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()
    
    init {
        loadInstalledApps()
    }
    
    /**
     * Load all installed apps
     */
    private fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val installedApps = getInstalledAppsUseCase.execute()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        installedApps = installedApps.sortedBy { app -> app.appName }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load installed apps"
                    )
                }
            }
        }
    }
    
    /**
     * Refresh the list of installed apps
     */
    fun refreshApps() {
        loadInstalledApps()
    }
}

/**
 * UI state for app selection screen
 */
data class AppSelectionUiState(
    val isLoading: Boolean = false,
    val installedApps: List<AppInfo> = emptyList(),
    val error: String? = null
)