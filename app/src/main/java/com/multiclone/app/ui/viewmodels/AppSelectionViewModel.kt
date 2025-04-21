package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.domain.models.InstalledApp
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
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
data class AppSelectionState(
    val installedApps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {

    // Private full list of apps
    private var allApps: List<InstalledApp> = emptyList()
    
    // UI state exposed to the composable
    private val _uiState = MutableStateFlow(AppSelectionState(isLoading = true))
    val uiState: StateFlow<AppSelectionState> = _uiState.asStateFlow()

    init {
        loadInstalledApps()
    }

    /**
     * Load the list of installed apps on the device
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                allApps = getInstalledAppsUseCase().filter { !it.isSelfApp }
                _uiState.update { 
                    it.copy(
                        installedApps = allApps,
                        isLoading = false
                    )
                }
                Timber.d("Loaded ${allApps.size} installed apps")
            } catch (e: Exception) {
                Timber.e(e, "Error loading installed apps")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load apps: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Search for apps matching the given query
     */
    fun searchApps(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(installedApps = allApps) }
                return@launch
            }
            
            val lowercaseQuery = query.lowercase()
            val filtered = allApps.filter { app ->
                app.appName.lowercase().contains(lowercaseQuery) ||
                app.packageName.lowercase().contains(lowercaseQuery)
            }
            
            _uiState.update { it.copy(installedApps = filtered) }
            Timber.d("Filtered to ${filtered.size} apps matching '$query'")
        }
    }

    /**
     * Sort apps by the given criteria
     */
    fun sortApps(sortBy: AppSortOption) {
        viewModelScope.launch {
            val sortedApps = when (sortBy) {
                AppSortOption.NAME -> allApps.sortedBy { it.appName }
                AppSortOption.PACKAGE -> allApps.sortedBy { it.packageName }
                AppSortOption.CLONEABILITY -> allApps.sortedByDescending { it.canBeCloned }
            }
            
            _uiState.update { it.copy(installedApps = sortedApps) }
        }
    }
}

/**
 * Sorting options for the app list
 */
enum class AppSortOption {
    NAME, PACKAGE, CLONEABILITY
}