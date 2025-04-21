package com.multiclone.app.domain.models

/**
 * Model representing an installed application on the device
 */
data class InstalledApp(
    // Package name of the app
    val packageName: String,
    
    // Display name of the app
    val appName: String,
    
    // Version name (e.g. "1.0.0")
    val versionName: String,
    
    // Version code (e.g. 10)
    val versionCode: Long,
    
    // Whether the app is a system app
    val isSystemApp: Boolean,
    
    // Number of clones created for this app
    val cloneCount: Int = 0,
    
    // Whether the app has been updated since last clone
    val hasUpdate: Boolean = false,
    
    // Installation time
    val installedTime: Long,
    
    // Last update time
    val lastUpdateTime: Long,
    
    // Installation source (store)
    val installSource: String? = null
)