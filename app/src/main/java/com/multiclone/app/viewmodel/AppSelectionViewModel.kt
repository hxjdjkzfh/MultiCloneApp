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
 * ViewModel for the AppSelectionScreen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppSelectionUiState())
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()

    /**
     * Loads the list of installed apps that can be cloned
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val apps = getInstalledAppsUseCase()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        apps = apps.sortedBy { app -> app.appName },
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    /**
     * Filters the list of apps based on a search query
     */
    fun filterApps(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadInstalledApps()
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val allApps = getInstalledAppsUseCase()
                val filteredApps = allApps.filter { app ->
                    app.appName.contains(query, ignoreCase = true) ||
                    app.packageName.contains(query, ignoreCase = true)
                }.sortedBy { it.appName }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        apps = filteredApps,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
}

/**
 * UI state for the AppSelectionScreen
 */
data class AppSelectionUiState(
    val isLoading: Boolean = false,
    val apps: List<AppInfo> = emptyList(),
    val error: String? = null
)