package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Represents information about an installed application
 */
@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val icon: @RawValue Drawable?,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val launchCount: Int = 0,
    val lastLaunchTime: Long = 0
) : Parcelable {
    
    /**
     * Returns true if the app is cloneable
     * Some system apps or apps with special permissions cannot be cloned
     */
    fun isCloneable(): Boolean {
        // Blacklist of non-cloneable packages
        val nonCloneablePackages = listOf(
            "com.multiclone.app",          // Don't clone itself
            "com.android.systemui",         // System UI
            "com.google.android.gsf",       // Google Services Framework
            "com.google.android.gms",       // Google Play Services
            "com.android.vending"           // Google Play Store
        )
        
        // Check if package is in blacklist
        if (packageName in nonCloneablePackages) {
            return false
        }
        
        // Additional checks for system apps - only allow certain system apps
        if (isSystemApp) {
            val allowedSystemApps = listOf(
                "com.android.chrome",
                "com.google.android.apps.maps",
                "com.google.android.apps.photos"
            )
            return packageName in allowedSystemApps
        }
        
        return true
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppInfo) return false
        
        return packageName == other.packageName
    }
    
    override fun hashCode(): Int {
        return packageName.hashCode()
    }
}