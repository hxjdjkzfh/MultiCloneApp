package com.multiclone.app.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing information about a cloned application
 */
@Parcelize
data class CloneInfo(
    val id: String,
    val packageName: String,
    val originalAppName: String,
    val displayName: String,
    val customIcon: Bitmap?,
    val virtualEnvironmentId: String,
    val creationTimestamp: Long,
    val lastUsedTimestamp: Long
) : Parcelable