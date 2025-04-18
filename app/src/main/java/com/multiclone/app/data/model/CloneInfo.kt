package com.multiclone.app.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Data model representing a cloned application
 */
@Parcelize
data class CloneInfo(
    val id: String,
    val packageName: String,
    val originalAppName: String,
    val displayName: String,
    val customIcon: @RawValue Bitmap?,
    val virtualEnvironmentId: String,
    val creationTimestamp: Long,
    var lastUsedTimestamp: Long
) : Parcelable