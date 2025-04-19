package com.multiclone.app.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the app selection screen
 */
data class AppSelectionUiState(
    val isLoading: Boolean = true,
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val selectedPackageName: String? = null,
    val isCreatingClone: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    
    // Private mutable state flow
    private val _uiState = MutableStateFlow(AppSelectionUiState())
    
    // Public immutable state flow
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()
    
    init {
        // Load apps when ViewModel is created
        loadInstalledApps()
    }
    
    /**
     * Loads all installed apps from the repository
     */
    private fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val installedApps = appRepository.getInstalledApps()
                
                // Filter to keep only apps that can be cloned
                val cloneableApps = installedApps.filter { it.isCloneable() }
                
                // Sort by app name
                val sortedApps = cloneableApps.sortedBy { it.appName.lowercase() }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        allApps = sortedApps,
                        filteredApps = sortedApps
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading installed apps")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading apps: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Filters the app list based on search query
     */
    fun filterApps(query: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            // Don't filter if apps haven't been loaded yet
            if (currentState.allApps.isEmpty()) return@launch
            
            if (query.isBlank()) {
                // No filter, show all apps
                _uiState.update { it.copy(filteredApps = it.allApps) }
            } else {
                // Filter by app name or package name
                val lowerQuery = query.lowercase()
                val filtered = currentState.allApps.filter { app ->
                    app.appName.lowercase().contains(lowerQuery) ||
                    app.packageName.lowercase().contains(lowerQuery)
                }
                _uiState.update { it.copy(filteredApps = filtered) }
            }
        }
    }
    
    /**
     * Selects an app to clone
     */
    fun selectApp(packageName: String) {
        viewModelScope.launch {
            // Find the app in our list
            val app = _uiState.value.allApps.find { it.packageName == packageName }
            
            if (app != null) {
                _uiState.update { it.copy(selectedPackageName = packageName) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Selected app not found"
                    )
                }
            }
        }
    }
    
    /**
     * Clears the current app selection
     */
    fun clearSelection() {
        _uiState.update { it.copy(selectedPackageName = null) }
    }
    
    /**
     * Dismisses the current error message
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    companion object {
        /**
         * Creates a preview version of the ViewModel with sample data
         */
        fun createPreview(): AppSelectionViewModel {
            val mockRepository = object : AppRepository {
                override suspend fun getInstalledApps(): List<AppInfo> {
                    return listOf(
                        AppInfo.createSimplified(
                            packageName = "com.example.app1",
                            appName = "Example App 1"
                        ),
                        AppInfo.createSimplified(
                            packageName = "com.example.app2",
                            appName = "Example App 2"
                        ),
                        AppInfo.createSimplified(
                            packageName = "com.example.app3",
                            appName = "Example App 3"
                        )
                    )
                }
                
                override suspend fun getAppInfo(packageName: String): AppInfo? {
                    return when (packageName) {
                        "com.example.app1" -> AppInfo.createSimplified(
                            packageName = "com.example.app1",
                            appName = "Example App 1"
                        )
                        "com.example.app2" -> AppInfo.createSimplified(
                            packageName = "com.example.app2",
                            appName = "Example App 2"
                        )
                        "com.example.app3" -> AppInfo.createSimplified(
                            packageName = "com.example.app3",
                            appName = "Example App 3"
                        )
                        else -> null
                    }
                }
                
                override suspend fun isAppInstalled(packageName: String): Boolean {
                    return packageName.startsWith("com.example.")
                }
            }
            
            return AppSelectionViewModel(mockRepository).apply {
                viewModelScope.launch {
                    val apps = mockRepository.getInstalledApps()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allApps = apps,
                            filteredApps = apps
                        )
                    }
                }
            }
        }
    }
}