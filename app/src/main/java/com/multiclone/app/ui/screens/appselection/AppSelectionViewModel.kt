package com.multiclone.app.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.core.virtualization.CloneManagerService
import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for App Selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) : ViewModel() {
    
    // List of all cloneable apps
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    // Filtered list of apps
    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filter state (0 = all, 1 = system, 2 = user)
    private val _filterState = MutableStateFlow(0)
    val filterState: StateFlow<Int> = _filterState.asStateFlow()
    
    /**
     * Load available apps
     */
    init {
        Timber.d("AppSelectionViewModel initialized")
        loadApps()
    }
    
    /**
     * Load the list of cloneable apps
     */
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cloneableApps = virtualAppEngine.getCloneableApps()
                _apps.value = cloneableApps
                applyFilters()
            } catch (e: Exception) {
                Timber.e(e, "Failed to load apps")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }
    
    /**
     * Update filter state
     */
    fun setFilterState(state: Int) {
        _filterState.value = state
        applyFilters()
    }
    
    /**
     * Apply search and filters to app list
     */
    private fun applyFilters() {
        val query = _searchQuery.value.lowercase()
        val filter = _filterState.value
        
        val filtered = _apps.value.filter { app ->
            // Apply search filter
            val matchesSearch = query.isEmpty() || 
                app.appName.lowercase().contains(query) || 
                app.packageName.lowercase().contains(query)
                
            // Apply type filter
            val matchesFilter = when (filter) {
                0 -> true // All apps
                1 -> app.isSystemApp // System apps
                2 -> !app.isSystemApp // User apps
                else -> true
            }
            
            matchesSearch && matchesFilter
        }
        
        _filteredApps.value = filtered.sortedBy { it.appName }
    }
    
    /**
     * Reset filters
     */
    fun resetFilters() {
        _searchQuery.value = ""
        _filterState.value = 0
        applyFilters()
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }
}