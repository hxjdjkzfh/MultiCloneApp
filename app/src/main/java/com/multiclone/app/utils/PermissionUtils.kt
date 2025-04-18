package com.multiclone.app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object PermissionUtils {
    /**
     * Check if a permission is granted.
     *
     * @param context The application context.
     * @param permission The permission to check.
     * @return True if the permission is granted, false otherwise.
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if multiple permissions are granted.
     *
     * @param context The application context.
     * @param permissions The permissions to check.
     * @return True if all permissions are granted, false otherwise.
     */
    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        return permissions.all { hasPermission(context, it) }
    }
    
    /**
     * Register a permission request launcher.
     *
     * @param activity The activity.
     * @param onResult Callback for the result of the permission request.
     * @return The permission request launcher.
     */
    fun registerPermissionLauncher(
        activity: AppCompatActivity,
        onResult: (Boolean) -> Unit
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            onResult
        )
    }
    
    /**
     * Register a multiple permissions request launcher.
     *
     * @param activity The activity.
     * @param onResult Callback for the result of the permission request.
     * @return The permission request launcher.
     */
    fun registerMultiplePermissionsLauncher(
        activity: AppCompatActivity,
        onResult: (Map<String, Boolean>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            onResult
        )
    }
    
    /**
     * Check if the app can create shortcuts.
     *
     * @param context The application context.
     * @return True if the app can create shortcuts, false otherwise.
     */
    fun canCreateShortcuts(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as android.content.pm.ShortcutManager
            return shortcutManager.isRequestPinShortcutSupported
        }
        
        // For pre-Oreo devices, we assume shortcuts can be created
        // This may not be accurate for all launcher apps
        return true
    }
}
