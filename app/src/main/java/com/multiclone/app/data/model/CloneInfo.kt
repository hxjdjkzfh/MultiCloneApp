package com.multiclone.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Information about a cloned application
 */
@Parcelize
data class CloneInfo(
    val id: String,
    val packageName: String,
    val displayName: String,
    val iconPath: String?, // Path to custom icon if set
    val creationTime: Long,
    val lastUsedTime: Long,
    val notificationsEnabled: Boolean,
    val storageLocation: String, // Path to the clone storage directory
    val customSettings: Map<String, String> = mapOf() // Additional settings
) : Parcelable {
    companion object {
        /**
         * Creates a new CloneInfo for a package with default settings
         */
        fun create(packageName: String, displayName: String, storageLocation: String): CloneInfo {
            val currentTime = System.currentTimeMillis()
            return CloneInfo(
                id = java.util.UUID.randomUUID().toString(),
                packageName = packageName,
                displayName = displayName,
                iconPath = null,
                creationTime = currentTime,
                lastUsedTime = currentTime,
                notificationsEnabled = true,
                storageLocation = storageLocation
            )
        }
    }
}