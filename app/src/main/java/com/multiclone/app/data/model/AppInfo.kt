package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Information about an installed application
 */
@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: @RawValue Drawable?,
    val sourceDir: String,
    val isSystem: Boolean
) : Parcelable