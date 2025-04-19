package com.multiclone.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a cloned application with additional configuration.
 */
@Parcelize
@Serializable
data class CloneInfo(
    /**
     * Unique identifier for this clone.
     */
    val id: String = UUID.randomUUID().toString(),
    
    /**
     * Package name of the original app.
     */
    val packageName: String,
    
    /**
     * Custom name for this clone, defaults to the original app name.
     */
    val customName: String,
    
    /**
     * Timestamp when this clone was created.
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * Timestamp when this clone was last launched.
     */
    val lastLaunchedAt: Long = 0L,
    
    /**
     * Count of how many times this clone has been launched.
     */
    val launchCount: Int = 0,
    
    /**
     * Theme color for this clone (in hex format).
     */
    val color: String = "#2196F3", // Default to Material Blue
    
    /**
     * Whether storage should be isolated for this clone.
     */
    val isolateStorage: Boolean = true,
    
    /**
     * Whether accounts should be isolated for this clone.
     */
    val isolateAccounts: Boolean = true,
    
    /**
     * Whether location should be isolated for this clone.
     */
    val isolateLocation: Boolean = false,
    
    /**
     * Whether a custom launcher shortcut should be created.
     */
    val createShortcut: Boolean = true,
    
    /**
     * Additional flags for virtualization behavior.
     */
    val virtualizationFlags: Map<String, Boolean> = emptyMap(),
    
    /**
     * Custom parameters for virtualization configuration.
     */
    val customParams: Map<String, String> = emptyMap()
) : Parcelable {
    /**
     * Checks if two clones refer to the same cloned app.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CloneInfo) return false
        return id == other.id
    }

    /**
     * Generates a hash code based on the clone's ID.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}