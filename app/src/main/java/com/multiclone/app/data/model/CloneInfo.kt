package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

/**
 * Represents information about a cloned app instance.
 * Contains the metadata and configuration for a specific clone.
 */
@Serializable
data class CloneInfo(
    val id: String,
    val packageName: String,
    val originalAppName: String,
    val cloneName: String,
    val creationTime: Long,
    val lastLaunchTime: Long = 0,
    val launchCount: Int = 0,
    val customIconPath: String? = null,
    val badgeColor: Int? = null,
    val notifications: Boolean = true,
    
    // Transient properties that aren't serialized
    @Transient
    val icon: Drawable? = null,
    @Transient
    val originalAppIcon: Drawable? = null
) {
    companion object {
        /**
         * Create a new CloneInfo with a unique ID
         */
        fun create(
            packageName: String,
            originalAppName: String,
            cloneName: String,
            customIconPath: String? = null,
            badgeColor: Int? = null
        ): CloneInfo {
            return CloneInfo(
                id = UUID.randomUUID().toString(),
                packageName = packageName,
                originalAppName = originalAppName,
                cloneName = cloneName,
                creationTime = System.currentTimeMillis(),
                customIconPath = customIconPath,
                badgeColor = badgeColor
            )
        }
    }
    
    /**
     * Creates a copy of the CloneInfo with updated last launch time and count
     */
    fun recordLaunch(): CloneInfo {
        return copy(
            lastLaunchTime = System.currentTimeMillis(),
            launchCount = launchCount + 1
        )
    }
    
    /**
     * Gets the effective name to display for this clone
     */
    fun getDisplayName(): String {
        return if (cloneName.isNotBlank()) {
            cloneName
        } else {
            "$originalAppName (Clone)"
        }
    }
}