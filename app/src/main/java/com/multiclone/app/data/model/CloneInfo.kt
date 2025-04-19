package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Data class representing information about a cloned app
 */
@Parcelize
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val originalPackageName: String,
    val cloneName: String,
    val customIcon: Boolean = false,
    val iconPath: String? = null,
    val creationTime: Long = System.currentTimeMillis(),
    val lastLaunchTime: Long = 0L,
    val launchCount: Int = 0,
    val isRunning: Boolean = false,
    val customSettings: Map<String, String> = emptyMap(),
    // Non-parcelable fields that don't need to be passed between components
    @Transient var icon: Drawable? = null
) : Parcelable {
    
    /**
     * Generate a unique package name for the clone
     */
    fun getClonePackageName(): String {
        return "$originalPackageName.clone_$id"
    }
    
    /**
     * Get the custom settings as a JSON string
     */
    fun getSettingsJson(): String {
        val settingsJson = StringBuilder()
        settingsJson.append("{")
        
        customSettings.entries.forEachIndexed { index, entry ->
            settingsJson.append("\"${entry.key}\": \"${entry.value}\"")
            if (index < customSettings.size - 1) {
                settingsJson.append(",")
            }
        }
        
        settingsJson.append("}")
        return settingsJson.toString()
    }
    
    /**
     * Create a copy of this CloneInfo with updated launch statistics
     */
    fun withUpdatedLaunchStats(): CloneInfo {
        return copy(
            lastLaunchTime = System.currentTimeMillis(),
            launchCount = launchCount + 1,
            isRunning = true
        )
    }
    
    /**
     * Create a copy of this CloneInfo with updated running status
     */
    fun withUpdatedRunningStatus(running: Boolean): CloneInfo {
        return copy(isRunning = running)
    }
    
    companion object {
        /**
         * Create a new CloneInfo from an AppInfo
         */
        fun fromAppInfo(appInfo: AppInfo, cloneName: String? = null): CloneInfo {
            return CloneInfo(
                originalPackageName = appInfo.packageName,
                cloneName = cloneName ?: "${appInfo.appName} (Clone)",
                customIcon = false,
                iconPath = null,
                icon = appInfo.icon
            )
        }
    }
}