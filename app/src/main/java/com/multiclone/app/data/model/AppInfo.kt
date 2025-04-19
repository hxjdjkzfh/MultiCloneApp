package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Represents information about an installed application.
 * Contains details needed for displaying and cloning apps.
 */
@Serializable
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0,
    val lastUpdateTime: Long = 0,
    
    // Transient properties that aren't serialized
    @Transient
    val appIcon: Drawable? = null
) {
    companion object {
        // Create a simplified app info object for testing or default values
        fun createSimplified(packageName: String, appName: String): AppInfo {
            return AppInfo(
                packageName = packageName,
                appName = appName,
                versionName = "1.0",
                versionCode = 1,
                isSystemApp = false,
                installTime = System.currentTimeMillis(),
                lastUpdateTime = System.currentTimeMillis()
            )
        }
    }
}