package com.multiclone.app.data.model

import android.graphics.Bitmap
import java.util.UUID

/**
 * Data class representing a cloned application.
 */
data class CloneInfo(
    val id: String = UUID.randomUUID().toString(),
    val originalPackageName: String,
    val originalAppName: String,
    val packageName: String,
    val name: String,
    val icon: Bitmap? = null,
    val creationDate: String,
    val lastLaunchDate: String? = null
)
