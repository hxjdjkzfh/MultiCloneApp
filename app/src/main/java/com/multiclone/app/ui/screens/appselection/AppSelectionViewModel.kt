package com.multiclone.app.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * View state for the app selection screen
 */
data class AppSelectionViewState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null,
    val selectedApp: AppInfo? = null
)

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val cloneRepository: CloneRepository
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow(AppSelectionViewState(isLoading = true))
    val uiState: StateFlow<AppSelectionViewState> = _uiState.asStateFlow()
    
    init {
        Timber.d("AppSelectionViewModel initialized")
        loadInstalledApps()
    }
    
    /**
     * Load all installed apps
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            Timber.d("Loading installed apps")
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val apps = withContext(Dispatchers.IO) {
                    cloneRepository.getInstalledApps().sortedBy { it.appName }
                }
                
                _uiState.update { 
                    it.copy(
                        apps = apps,
                        filteredApps = apps,
                        isLoading = false
                    ) 
                }
                
                Timber.d("Loaded ${apps.size} installed apps")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load installed apps")
                _uiState.update { 
                    it.copy(
                        error = "Failed to load installed apps: ${e.message}",
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    /**
     * Update the search query
     */
    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            filterApps()
        }
    }
    
    /**
     * Filter apps based on the current search query
     */
    private fun filterApps() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        
        if (query.isEmpty()) {
            // If the query is empty, show all apps
            _uiState.update { it.copy(filteredApps = it.apps) }
            return
        }
        
        // Filter apps by name or package name
        val filteredApps = _uiState.value.apps.filter { app ->
            app.appName.lowercase().contains(query) || 
            app.packageName.lowercase().contains(query)
        }
        
        _uiState.update { it.copy(filteredApps = filteredApps) }
        Timber.d("Filtered to ${filteredApps.size} apps")
    }
    
    /**
     * Check if an app can be cloned (e.g., hasn't reached the limit)
     */
    fun canCloneApp(packageName: String): Boolean {
        // In a real implementation, we would check if the app has reached its clone limit
        // For now, we'll assume unlimited clones are allowed
        return true
    }
    
    /**
     * Get the clone count for an app
     */
    fun getCloneCountForApp(packageName: String): Int {
        return cloneRepository.getCloneCountForPackage(packageName)
    }
    
    /**
     * Select an app for cloning
     */
    fun selectApp(app: AppInfo?) {
        _uiState.update { it.copy(selectedApp = app) }
    }
    
    /**
     * Clear any error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("AppSelectionViewModel cleared")
    }
}