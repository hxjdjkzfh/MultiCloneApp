package com.multiclone.app.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Data class representing information about a cloned app
 *
 * @property id Unique identifier for the clone
 * @property packageName The package name of the original app
 * @property originalAppName The display name of the original app
 * @property customName The custom name for the clone (optional)
 * @property icon The custom icon bitmap for the clone (optional)
 * @property cloneIndex The index of this clone (for multiple clones of the same app)
 * @property createdAt Timestamp when this clone was created
 * @property lastUsedAt Timestamp when this clone was last used
 */
@Parcelize
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val originalAppName: String,
    val customName: String? = null,
    val icon: Bitmap? = null,
    val cloneIndex: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Gets the display name for the clone
     * Uses the custom name if set, otherwise uses the original app name with clone index
     */
    val displayName: String
        get() = customName ?: "$originalAppName ($cloneIndex)"
}