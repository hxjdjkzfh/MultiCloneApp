package com.multiclone.app.data.repository

import com.multiclone.app.data.model.AppInfo

/**
 * Repository interface for accessing application information.
 */
interface AppRepository {
    /**
     * Gets a list of all installed (non-system) apps on the device.
     * 
     * @return List of app information objects
     */
    suspend fun getInstalledApps(): List<AppInfo>
    
    /**
     * Gets app info for a single package.
     * 
     * @param packageName The package name to look up
     * @return App info or null if not found or error
     */
    suspend fun getAppInfo(packageName: String): AppInfo?
    
    /**
     * Checks if an app is installed on the device.
     * 
     * @param packageName The package name to check
     * @return True if the app is installed
     */
    suspend fun isAppInstalled(packageName: String): Boolean
}