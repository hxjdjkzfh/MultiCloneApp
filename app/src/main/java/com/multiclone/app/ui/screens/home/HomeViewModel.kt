package com.multiclone.app.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.data.repository.CloneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * UI state for the Home Screen
 */
data class HomeUiState(
    val clones: List<CloneInfo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the Home screen.
 * Manages the list of cloned apps and operations on them.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cloneRepository: CloneRepository,
    private val virtualAppEngine: VirtualAppEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        Timber.d("HomeViewModel created")
        loadClones()
        
        // Observe clone repository for changes
        viewModelScope.launch {
            cloneRepository.clones.collect { clones ->
                _uiState.update { currentState ->
                    currentState.copy(
                        clones = clones,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Load clones from the repository
     */
    private fun loadClones() {
        Timber.d("Loading clones")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                cloneRepository.loadClones()
                _uiState.update { it.copy(errorMessage = null) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading clones")
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to load clones: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * Launches a cloned app
     * 
     * @param cloneId ID of the clone to launch
     */
    fun launchClone(cloneId: String) {
        Timber.d("Launching clone $cloneId")
        viewModelScope.launch {
            try {
                val clone = cloneRepository.getCloneById(cloneId)
                if (clone != null) {
                    // Create launch intent
                    val intent = virtualAppEngine.getLaunchIntent(clone)
                    
                    // Start the activity
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    
                    // Update the clone's launch count and timestamp
                    val updatedClone = clone.copy(
                        launchCount = clone.launchCount + 1,
                        lastLaunchedAt = System.currentTimeMillis()
                    )
                    cloneRepository.updateClone(updatedClone)
                } else {
                    Timber.e("Clone not found: $cloneId")
                    _uiState.update { 
                        it.copy(errorMessage = "Clone not found") 
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error launching clone $cloneId")
                _uiState.update { 
                    it.copy(errorMessage = "Failed to launch clone: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Deletes a cloned app
     * 
     * @param cloneId ID of the clone to delete
     */
    fun deleteClone(cloneId: String) {
        Timber.d("Deleting clone $cloneId")
        viewModelScope.launch {
            try {
                // Remove the clone from the virtual environment
                virtualAppEngine.removeClone(cloneId)
                
                // Remove the clone from the repository
                val success = cloneRepository.removeClone(cloneId)
                
                if (!success) {
                    Timber.e("Failed to remove clone $cloneId from repository")
                    _uiState.update { 
                        it.copy(errorMessage = "Failed to remove clone") 
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error deleting clone $cloneId")
                _uiState.update { 
                    it.copy(errorMessage = "Failed to delete clone: ${e.message}") 
                }
            }
        }
    }
    
    /**
     * Refreshes the list of clones
     */
    fun refreshClones() {
        loadClones()
    }
}