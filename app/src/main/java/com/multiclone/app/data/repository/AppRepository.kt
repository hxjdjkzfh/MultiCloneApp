package com.multiclone.app.data.repository

import com.multiclone.app.core.virtualization.VirtualAppEngine
import com.multiclone.app.data.model.AppInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing installed applications data
 */
@Singleton
class AppRepository @Inject constructor(
    private val virtualAppEngine: VirtualAppEngine
) {
    /**
     * Get a list of all installed apps that can be cloned
     */
    fun getInstalledApps(): List<AppInfo> {
        return virtualAppEngine.getInstalledApps()
    }
    
    /**
     * Search installed apps by name or package
     */
    fun searchInstalledApps(query: String): List<AppInfo> {
        val normalizedQuery = query.trim().lowercase()
        
        if (normalizedQuery.isEmpty()) {
            return getInstalledApps()
        }
        
        return getInstalledApps().filter { app ->
            app.appName.lowercase().contains(normalizedQuery) ||
            app.packageName.lowercase().contains(normalizedQuery)
        }
    }
    
    /**
     * Get app info for a specific package
     */
    fun getAppInfo(packageName: String): AppInfo? {
        return getInstalledApps().find { it.packageName == packageName }
    }
}