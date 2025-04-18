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
    val environmentId: String = id // Environment ID is same as clone ID for simplicity
)