package com.multiclone.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.multiclone.app.CloneProxyActivity
import com.multiclone.app.data.repository.CloneRepository
import com.multiclone.app.domain.virtualization.CloneEnvironment
import com.multiclone.app.utils.IconUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for creating shortcuts to launch cloned apps
 */
class CreateShortcutUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloneRepository: CloneRepository,
    private val cloneEnvironment: CloneEnvironment,
    private val iconUtils: IconUtils
) {
    private val TAG = "CreateShortcutUseCase"
    
    /**
     * Create a shortcut for a cloned app
     * @param cloneId The ID of the clone to create a shortcut for
     * @return True if shortcut was created successfully, false otherwise
     */
    suspend operator fun invoke(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Creating shortcut for clone $cloneId")
            
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: run {
                Log.e(TAG, "Clone not found: $cloneId")
                return@withContext false
            }
            
            // Get environment ID for this clone
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId) ?: run {
                // If environment doesn't exist yet, use the clone ID as the environment ID
                // (will be created when app is first launched)
                cloneId
            }
            
            // Create intent for launching the cloned app via proxy
            val launchIntent = CloneProxyActivity.createShortcutIntent(
                context = context,
                packageName = cloneInfo.packageName,
                environmentId = environmentId,
                cloneId = cloneId,
                customLabel = cloneInfo.cloneName
            )
            
            // Get icon for the shortcut
            val icon = when {
                // Use custom icon if available
                cloneInfo.customIcon != null -> {
                    Log.d(TAG, "Using custom icon for clone $cloneId")
                    IconCompat.createWithBitmap(cloneInfo.customIcon)
                }
                
                // Otherwise, use app icon with badge
                else -> {
                    Log.d(TAG, "Using default icon with badge for clone $cloneId")
                    val defaultIcon = iconUtils.getAppIcon(cloneInfo.packageName)
                    if (defaultIcon != null) {
                        // If we have a badge number, apply it
                        val badgeNumber = cloneInfo.badgeNumber ?: "2"
                        IconCompat.createWithBitmap(
                            iconUtils.drawBadge(defaultIcon, badgeNumber)
                        )
                    } else {
                        Log.e(TAG, "Failed to get app icon for ${cloneInfo.packageName}")
                        null
                    }
                }
            }
            
            if (icon == null) {
                Log.e(TAG, "No icon available for shortcut")
                return@withContext false
            }
            
            // Create and add the shortcut
            val shortcutId = "clone_${cloneInfo.id}"
            val shortcutInfo = ShortcutInfoCompat.Builder(context, shortcutId)
                .setShortLabel(cloneInfo.cloneName)
                .setLongLabel(cloneInfo.cloneName)
                .setIcon(icon)
                .setIntent(launchIntent)
                .build()
            
            // Request the shortcut to be pinned
            Log.d(TAG, "Requesting pin shortcut for clone $cloneId")
            val added = ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
            
            // Update shortcut status in clone info
            if (added) {
                Log.d(TAG, "Shortcut created successfully for clone $cloneId")
                cloneRepository.updateShortcutStatus(cloneId, true)
            } else {
                Log.e(TAG, "Failed to create shortcut for clone $cloneId")
            }
            
            added
        } catch (e: Exception) {
            Log.e(TAG, "Error creating shortcut", e)
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Create a dynamic shortcut for a cloned app
     * This doesn't require user interaction to pin the shortcut
     */
    suspend fun createDynamicShortcut(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                // Dynamic shortcuts require Android 7.1+
                return@withContext false
            }
            
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return@withContext false
            
            // Get environment ID
            val environmentId = cloneEnvironment.getEnvironmentIdForClone(cloneId) ?: cloneId
            
            // Create intent for launching the cloned app
            val launchIntent = CloneProxyActivity.createShortcutIntent(
                context = context,
                packageName = cloneInfo.packageName,
                environmentId = environmentId,
                cloneId = cloneId,
                customLabel = cloneInfo.cloneName
            )
            
            // Get shortcut icon
            val icon = when {
                cloneInfo.customIcon != null -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Icon.createWithBitmap(cloneInfo.customIcon)
                    } else {
                        null
                    }
                }
                else -> {
                    val appIcon = iconUtils.getAppIcon(cloneInfo.packageName)
                    if (appIcon != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val badgeNumber = cloneInfo.badgeNumber ?: "2"
                        Icon.createWithBitmap(iconUtils.drawBadge(appIcon, badgeNumber))
                    } else {
                        null
                    }
                }
            } ?: return@withContext false
            
            // Create shortcut info
            val shortcutId = "clone_${cloneInfo.id}"
            val shortcutInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                ShortcutInfo.Builder(context, shortcutId)
                    .setShortLabel(cloneInfo.cloneName)
                    .setLongLabel(cloneInfo.cloneName)
                    .setIcon(icon)
                    .setIntent(launchIntent)
                    .build()
            } else {
                return@withContext false
            }
            
            // Add the dynamic shortcut
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            shortcutManager?.let {
                // Remove existing shortcut with same ID if it exists
                val existingShortcuts = shortcutManager.dynamicShortcuts
                    .filter { it.id == shortcutId }
                    .map { it.id }
                
                if (existingShortcuts.isNotEmpty()) {
                    shortcutManager.removeDynamicShortcuts(existingShortcuts)
                }
                
                // Add the new shortcut
                shortcutManager.addDynamicShortcuts(listOf(shortcutInfo))
                
                // Update shortcut status in clone info
                cloneRepository.updateShortcutStatus(cloneId, true)
                
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error creating dynamic shortcut", e)
            e.printStackTrace()
            false
        }
    }
}