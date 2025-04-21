package com.multiclone.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.multiclone.app.domain.models.ClonedApp
import com.multiclone.app.virtualization.VirtualAppEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * States representing the UI state for the home screen.
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val clones: List<ClonedApp>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel for the home screen.
 * Manages the state of cloned apps and interactions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadClones()
    }
    
    /**
     * Attaches this ViewModel to a lifecycle to properly handle lifecycle events
     * This is important for Android 14 where lifecycle awareness is more strict
     */
    fun attachToLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                // Refresh clones list when app comes to foreground
                loadClones()
            }
            
            override fun onStop(owner: LifecycleOwner) {
                // Clean up resources if needed
            }
        })
    }

    /**
     * Loads all cloned apps.
     */
    fun loadClones() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                
                // Initialize the engine if needed
                virtualAppEngine.initialize()
                
                // Get all clones
                val clones = virtualAppEngine.getAllClones()
                
                if (clones.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = HomeUiState.Success(clones)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading clones")
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Failed to load cloned apps"
                )
            }
        }
    }

    /**
     * Launches a cloned app.
     */
    fun launchClone(cloneId: String) {
        viewModelScope.launch {
            try {
                // Find the clone
                val currentState = uiState.value
                if (currentState is HomeUiState.Success) {
                    val cloneToLaunch = currentState.clones.find { it.id == cloneId }
                    
                    if (cloneToLaunch != null) {
                        // Launch the clone
                        virtualAppEngine.launchClone(cloneToLaunch)
                    } else {
                        Timber.e("Clone not found: $cloneId")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error launching clone: $cloneId")
            }
        }
    }

    /**
     * Removes a cloned app.
     */
    fun removeClone(cloneId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                
                // Remove the clone
                val success = virtualAppEngine.removeClone(cloneId)
                
                if (success) {
                    // Reload the clones
                    loadClones()
                } else {
                    _uiState.value = HomeUiState.Error("Failed to remove the cloned app")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error removing clone: $cloneId")
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Failed to remove the cloned app"
                )
            }
        }
    }
}