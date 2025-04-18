package com.multiclone.app.data.model

import android.graphics.Bitmap
import java.util.UUID

/**
 * Represents information about a cloned application
 */
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val originalAppName: String,
    val cloneName: String,
    val customIcon: Bitmap? = null,
    val creationTime: Long = System.currentTimeMillis(),
    val lastUsedTime: Long = 0L,
    val hasShortcut: Boolean = false,
    val environmentId: String = id, // Environment ID may differ from clone ID for security
    val badgeNumber: String? = "2", // Badge number to show on the icon
    val launchCount: Int? = 0, // Number of times this clone has been launched
    val isEnabled: Boolean = true, // Whether this clone is currently enabled
    val storagePath: String? = null // Custom storage path if applicable
)