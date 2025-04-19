package com.multiclone.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the home screen
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val clones: List<CloneInfo> = emptyList(),
    val isOperationInProgress: Boolean = false,
    val operationMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel for the home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {
    
    // Private mutable state flow
    private val _uiState = MutableStateFlow(HomeUiState())
    
    // Public immutable state flow
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // Load clones when ViewModel is created
        loadClones()
    }
    
    /**
     * Loads clones from the repository
     */
    private fun loadClones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // First load from storage
                cloneRepository.loadClones()
                
                // Then collect updates
                cloneRepository.clones.collect { clones ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            clones = clones
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading clones")
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading clones: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Launches a cloned app
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            // Set operation in progress
            _uiState.update { 
                it.copy(
                    isOperationInProgress = true,
                    operationMessage = "Launching app..."
                )
            }
            
            try {
                // Get the clone
                val clone = cloneRepository.getCloneById(cloneId)
                if (clone == null) {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            errorMessage = "Clone not found"
                        )
                    }
                    return@launch
                }
                
                // Launch the clone
                val success = virtualAppEngine.launchClone(clone)
                
                if (success) {
                    // Update launch stats
                    val updatedClone = clone.updateLaunchStats()
                    cloneRepository.updateClone(updatedClone)
                } else {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            errorMessage = "Failed to launch app"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error launching clone $cloneId")
                _uiState.update {
                    it.copy(
                        isOperationInProgress = false,
                        errorMessage = "Error launching app: ${e.message}"
                    )
                }
            } finally {
                // Operation complete
                _uiState.update { it.copy(isOperationInProgress = false, operationMessage = null) }
            }
        }
    }
    
    /**
     * Deletes a cloned app
     */
    fun deleteClone(cloneId: String) {
        viewModelScope.launch {
            // Set operation in progress
            _uiState.update { 
                it.copy(
                    isOperationInProgress = true, 
                    operationMessage = "Deleting clone..."
                )
            }
            
            try {
                // Get the clone first
                val clone = cloneRepository.getCloneById(cloneId)
                if (clone == null) {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            errorMessage = "Clone not found"
                        )
                    }
                    return@launch
                }
                
                // Remove from virtualization engine
                val engineSuccess = virtualAppEngine.removeClone(clone)
                if (!engineSuccess) {
                    Timber.w("Failed to remove clone from virtual engine, continuing with repository removal")
                }
                
                // Remove from repository
                val repoSuccess = cloneRepository.removeClone(cloneId)
                
                if (!repoSuccess) {
                    _uiState.update {
                        it.copy(
                            isOperationInProgress = false,
                            errorMessage = "Failed to remove clone from repository"
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting clone $cloneId")
                _uiState.update {
                    it.copy(
                        isOperationInProgress = false,
                        errorMessage = "Error deleting clone: ${e.message}"
                    )
                }
            } finally {
                // Operation complete
                _uiState.update { it.copy(isOperationInProgress = false, operationMessage = null) }
            }
        }
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
        fun createPreview(): HomeViewModel {
            val mockRepository = object : CloneRepository {
                override val clones = MutableStateFlow(
                    listOf(
                        CloneInfo(
                            id = "1",
                            packageName = "com.example.app1",
                            originalAppName = "Example App 1",
                            cloneName = "Work Profile",
                            creationTime = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
                            lastLaunchTime = System.currentTimeMillis() - 3600000, // 1 hour ago
                            launchCount = 10
                        ),
                        CloneInfo(
                            id = "2",
                            packageName = "com.example.app2",
                            originalAppName = "Example App 2",
                            creationTime = System.currentTimeMillis() - 86400000, // 1 day ago
                            lastLaunchTime = 0, // Never launched
                            launchCount = 0
                        )
                    )
                )
                
                override suspend fun addClone(clone: CloneInfo): Boolean = true
                override suspend fun updateClone(clone: CloneInfo): Boolean = true
                override suspend fun removeClone(cloneId: String): Boolean = true
                override suspend fun getCloneById(cloneId: String): CloneInfo? = null
                override suspend fun loadClones(): Boolean = true
            }
            
            val mockEngine = object : VirtualAppEngine {
                override suspend fun installClone(packageName: String, cloneInfo: CloneInfo): Boolean = true
                override suspend fun launchClone(cloneInfo: CloneInfo): Boolean = true
                override suspend fun removeClone(cloneInfo: CloneInfo): Boolean = true
                override suspend fun isCloneInstalled(cloneInfo: CloneInfo): Boolean = true
                override suspend fun updateCloneSettings(cloneInfo: CloneInfo): Boolean = true
            }
            
            return HomeViewModel(mockRepository, mockEngine)
        }
    }
}