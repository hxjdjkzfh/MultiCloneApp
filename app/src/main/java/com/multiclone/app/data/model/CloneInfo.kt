package com.multiclone.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Represents information about a cloned application instance
 */
@Parcelize
data class CloneInfo(
    // Unique identifier for this clone
    val id: String = UUID.randomUUID().toString(),
    
    // Original app package name
    val originalPackageName: String,
    
    // Custom name given to this clone
    val customName: String,
    
    // Timestamp when this clone was created
    val createdAt: Long = System.currentTimeMillis(),
    
    // Timestamp when this clone was last launched
    val lastLaunchTime: Long = 0,
    
    // Number of times this clone has been launched
    val launchCount: Int = 0,
    
    // Custom icon resource ID or path (if any)
    val customIconPath: String? = null,
    
    // Custom color for this clone
    val customColor: Int? = null,
    
    // Storage isolation level (0=shared, 1=isolated, 2=fully isolated)
    val storageIsolationLevel: Int = 1,
    
    // Should the clone show custom notifications
    val useCustomNotifications: Boolean = true,
    
    // Should the clone be shown in launcher
    val showInLauncher: Boolean = true,
    
    // Environment version - used for upgrading clone environments
    val environmentVersion: Int = 1,
    
    // Is this clone running
    val isRunning: Boolean = false
) : Parcelable {
    
    /**
     * Get the internal package name used for this clone
     */
    fun getInternalPackageName(): String {
        return "com.multiclone.app.virtual.$originalPackageName.$id"
    }
    
    /**
     * Get the display name for this clone
     */
    fun getDisplayName(): String {
        return customName.ifEmpty { originalPackageName.split(".").last() }
    }
    
    /**
     * Check if the clone environment needs an update
     */
    fun needsEnvironmentUpdate(currentVersion: Int): Boolean {
        return environmentVersion < currentVersion
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CloneInfo) return false
        
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }
}