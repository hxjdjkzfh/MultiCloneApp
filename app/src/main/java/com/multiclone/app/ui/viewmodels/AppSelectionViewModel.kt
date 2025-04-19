package com.multiclone.app.ui.viewmodels

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiclone.app.data.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for app selection screen
 */
@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // List of all installed apps
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    // Filtered apps based on search query
    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()
    
    // Current search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error message
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadInstalledApps()
    }
    
    /**
     * Load all installed apps from the device
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val installedApps = fetchInstalledApps()
                _apps.value = installedApps
                // Initialize filtered apps with all apps
                updateFilteredApps()
            } catch (e: Exception) {
                _error.value = "Error loading apps: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update search query and filter apps
     */
    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            updateFilteredApps()
        }
    }
    
    /**
     * Filter apps based on current search query
     */
    private fun updateFilteredApps() {
        val query = _searchQuery.value.trim().lowercase()
        
        _filteredApps.value = if (query.isEmpty()) {
            _apps.value
        } else {
            _apps.value.filter {
                it.appName.lowercase().contains(query) ||
                it.packageName.lowercase().contains(query)
            }
        }
    }
    
    /**
     * Clear the error message
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Fetch all installed apps from the device
     */
    private suspend fun fetchInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<AppInfo>()
        
        try {
            // For API 33+ consider using PackageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
            val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            
            for (applicationInfo in applications) {
                // Skip system apps if not in launchable apps
                if (isSystemApp(applicationInfo) && !isLaunchableApp(packageManager, applicationInfo.packageName)) {
                    continue
                }
                
                // Skip our own app
                if (applicationInfo.packageName == context.packageName) {
                    continue
                }
                
                val appName = applicationInfo.loadLabel(packageManager).toString()
                val packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, 0)
                
                val appInfo = AppInfo(
                    packageName = applicationInfo.packageName,
                    appName = appName,
                    versionName = packageInfo.versionName ?: "",
                    versionCode = packageInfo.longVersionCode,
                    isSystemApp = isSystemApp(applicationInfo),
                    appIcon = applicationInfo.loadIcon(packageManager)
                )
                
                installedApps.add(appInfo)
            }
        } catch (e: Exception) {
            // Handle errors
        }
        
        // Sort alphabetically by app name
        installedApps.sortBy { it.appName.lowercase() }
        
        return@withContext installedApps
    }
    
    /**
     * Check if an app is a system app
     */
    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
    }
    
    /**
     * Check if an app can be launched
     */
    private fun isLaunchableApp(packageManager: PackageManager, packageName: String): Boolean {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return intent != null
    }
}