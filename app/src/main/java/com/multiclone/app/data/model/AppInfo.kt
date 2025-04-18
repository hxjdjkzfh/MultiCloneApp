package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Data model representing an installed application
 */
@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String?,
    val icon: @RawValue Drawable,
    val isSystemApp: Boolean
) : Parcelable