package com.multiclone.app.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.domain.models.AppInfo
import com.multiclone.app.virtualization.VirtualAppEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI states for the App Selection screen
 */
sealed class AppSelectionUiState {
    object Loading : AppSelectionUiState()
    data class Success(val apps: List<AppInfo>) : AppSelectionUiState()
    data class Error(val message: String) : AppSelectionUiState()
}

/**
 * ViewModel for the app selection screen.
 * Handles loading installed apps and filtering.
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppSelectionUiState>(AppSelectionUiState.Loading)
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()
    
    // List of all available apps
    private var allApps: List<AppInfo> = emptyList()
    
    // Current filter query
    private var currentQuery: String = ""
    
    init {
        loadInstalledApps()
    }
    
    /**
     * Loads all installed apps that can be cloned
     */
    private fun loadInstalledApps() {
        viewModelScope.launch {
            try {
                _uiState.value = AppSelectionUiState.Loading
                
                // Initialize the virtual app engine
                virtualAppEngine.initialize()
                
                // Get all cloneable apps
                allApps = virtualAppEngine.getCloneableApps()
                
                // Apply any existing filter
                applyFilter(currentQuery)
                
            } catch (e: Exception) {
                Timber.e(e, "Error loading installed apps")
                _uiState.value = AppSelectionUiState.Error(
                    e.message ?: "Failed to load installed apps"
                )
            }
        }
    }
    
    /**
     * Filters the app list based on a search query
     */
    fun filterApps(query: String) {
        currentQuery = query
        applyFilter(query)
    }
    
    /**
     * Applies the current filter to the app list
     */
    private fun applyFilter(query: String) {
        if (allApps.isEmpty()) {
            _uiState.value = AppSelectionUiState.Success(emptyList())
            return
        }
        
        if (query.isBlank()) {
            // No filter, show all apps
            _uiState.value = AppSelectionUiState.Success(allApps)
        } else {
            // Filter based on name or package
            val filtered = allApps.filter { app ->
                app.appName.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
            }
            _uiState.value = AppSelectionUiState.Success(filtered)
        }
    }
    
    /**
     * Refreshes the list of apps
     */
    fun refreshApps() {
        loadInstalledApps()
    }
}