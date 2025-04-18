package com.multiclone.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase
) : ViewModel() {
    
    private val _appsList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appsList: StateFlow<List<AppInfo>> = _appsList.asStateFlow()
    
    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Load all installed apps
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            getInstalledAppsUseCase(includeSystemApps = false).collectLatest { apps ->
                _appsList.value = apps
                _filteredApps.value = apps
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Filter apps based on search query
     */
    fun filterApps(query: String) {
        if (query.isBlank()) {
            _filteredApps.value = _appsList.value
            return
        }
        
        viewModelScope.launch {
            val lowercaseQuery = query.lowercase()
            _filteredApps.value = _appsList.value.filter { app ->
                app.appName.lowercase().contains(lowercaseQuery) ||
                app.packageName.lowercase().contains(lowercaseQuery)
            }
        }
    }
}