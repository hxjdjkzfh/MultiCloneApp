package com.multiclone.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.domain.models.ClonedApp
import com.multiclone.app.domain.usecase.GetClonedAppsUseCase
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * State holder for the HomeScreen
 */
data class HomeScreenState(
    val clonedApps: List<ClonedApp> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the HomeScreen that manages cloned apps
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getClonedAppsUseCase: GetClonedAppsUseCase,
    private val launchCloneUseCase: LaunchCloneUseCase
) : ViewModel() {

    // UI state exposed to the composable
    private val _uiState = MutableStateFlow(HomeScreenState(isLoading = true))
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        loadClonedApps()
    }

    /**
     * Load the list of cloned apps
     */
    fun loadClonedApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val clonedApps = getClonedAppsUseCase()
                _uiState.update { 
                    it.copy(
                        clonedApps = clonedApps,
                        isLoading = false
                    )
                }
                Timber.d("Loaded ${clonedApps.size} cloned apps")
            } catch (e: IOException) {
                Timber.e(e, "Error loading cloned apps")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load cloned apps: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Launch a cloned app
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            try {
                launchCloneUseCase(cloneId)
                Timber.d("Launching clone $cloneId")
                // Update the running status
                refreshCloneStatus(cloneId, true)
            } catch (e: Exception) {
                Timber.e(e, "Error launching clone $cloneId")
                _uiState.update { 
                    it.copy(error = "Failed to launch app: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Refresh the status of a specific clone
     */
    private fun refreshCloneStatus(cloneId: String, isRunning: Boolean) {
        _uiState.update { currentState ->
            val updatedList = currentState.clonedApps.map { clonedApp ->
                if (clonedApp.uniqueIdentifier == cloneId) {
                    clonedApp.copy(isRunning = isRunning)
                } else {
                    clonedApp
                }
            }
            currentState.copy(clonedApps = updatedList)
        }
    }
}