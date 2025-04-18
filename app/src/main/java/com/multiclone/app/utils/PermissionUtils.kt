package com.multiclone.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for handling runtime permissions
 */
@Singleton
class PermissionUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Check if all required permissions are granted
     */
    fun checkRequiredPermissions(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Get list of required permissions based on Android version
     */
    fun getRequiredPermissions(): List<String> {
        val basePermissions = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        // Add POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            basePermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // Add QUERY_ALL_PACKAGES permission for Android 11+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            basePermissions.add("android.permission.QUERY_ALL_PACKAGES")
        }
        
        return basePermissions
    }
    
    /**
     * Register permission launchers for an activity
     */
    fun registerPermissionLaunchers(
        activity: ComponentActivity,
        onPermissionsGranted: () -> Unit,
        onPermissionsDenied: () -> Unit
    ) {
        val requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
        
        // Launch permission request
        val permissions = getRequiredPermissions().toTypedArray()
        requestPermissionLauncher.launch(permissions)
    }
    
    /**
     * Open app settings to allow user to grant permissions manually
     */
    fun openAppSettings(activity: ComponentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}