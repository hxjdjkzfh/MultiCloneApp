package com.multiclone.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.multiclone.app.CloneProxyActivity
import com.multiclone.app.data.repository.CloneRepository
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
    private val iconUtils: IconUtils
) {
    /**
     * Create a shortcut for a cloned app
     * @param cloneId The ID of the clone to create a shortcut for
     * @return True if shortcut was created successfully, false otherwise
     */
    suspend operator fun invoke(cloneId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get clone info
            val cloneInfo = cloneRepository.getCloneById(cloneId) ?: return@withContext false
            
            // Create intent for launching the cloned app
            val launchIntent = Intent(context, CloneProxyActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("CLONE_ID", cloneId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            // Get icon for the shortcut
            val icon = cloneInfo.customIcon?.let {
                IconCompat.createWithBitmap(it)
            } ?: iconUtils.getAppIcon(cloneInfo.packageName)?.let {
                IconCompat.createWithBitmap(
                    iconUtils.drawBadge(it, "2")
                )
            }
            
            if (icon == null) {
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
            
            val added = ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
            
            // Update shortcut status in clone info
            if (added) {
                cloneRepository.updateShortcutStatus(cloneId, true)
            }
            
            added
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}