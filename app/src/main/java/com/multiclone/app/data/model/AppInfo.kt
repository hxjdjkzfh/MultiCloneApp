package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

/**
 * Model class representing an installed app on the device.
 * Contains all relevant information about an app that can be cloned.
 */
@Serializable
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String = "",
    val versionCode: Long = 0,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0,
    val lastUpdateTime: Long = 0,
    
    // Transient properties (not serialized)
    @Transient
    val appIcon: Drawable? = null
) {
    companion object {
        /**
         * Creates a simplified AppInfo with just the essential fields
         */
        fun createSimplified(
            packageName: String,
            appName: String
        ): AppInfo {
            return AppInfo(
                packageName = packageName,
                appName = appName
            )
        }
    }
    
    /**
     * Returns whether this app can be cloned
     * System apps and special apps cannot be cloned
     */
    fun isCloneable(): Boolean {
        // System apps are generally not cloneable
        if (isSystemApp) return false
        
        // These are special packages that should not be cloned
        val blacklistedPackages = listOf(
            "com.multiclone.app", // This app itself
            "com.android.", // Android system
            "com.google.android.", // Google system
            "android", // Android core
            "com.sec.android." // Samsung system
        )
        
        // Check if the package is in the blacklist
        for (prefix in blacklistedPackages) {
            if (packageName.startsWith(prefix)) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * Returns whether this app is from Google
     */
    fun isGoogleApp(): Boolean {
        return packageName.startsWith("com.google.")
    }
    
    /**
     * Generates a unique clone ID based on the package name
     */
    fun generateCloneId(): String {
        return UUID.randomUUID().toString()
    }
}