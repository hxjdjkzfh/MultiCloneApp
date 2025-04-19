package com.multiclone.app.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing information about an installed app
 */
@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installTime: Long,
    val updateTime: Long,
    val appSize: Long,
    // Non-parcelable fields that don't need to be passed between components
    @Transient var icon: Drawable? = null,
    @Transient var isSelected: Boolean = false
) : Parcelable {
    
    /**
     * Return a display-friendly version string
     */
    fun getVersionString(): String {
        return "$versionName ($versionCode)"
    }
    
    /**
     * Return a display-friendly size string
     */
    fun getSizeString(): String {
        val sizeInMb = appSize / (1024L * 1024L)
        return if (sizeInMb < 1) {
            val sizeInKb = appSize / 1024L
            "${sizeInKb}KB"
        } else {
            "${sizeInMb}MB"
        }
    }
}