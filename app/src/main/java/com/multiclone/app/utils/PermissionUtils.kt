package com.multiclone.app.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Utility class for handling app permissions
 */
object PermissionUtils {
    
    /**
     * Get the list of permissions required by the app
     * @return array of required permissions
     */
    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf<String>()
        
        // Required for cloning apps
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        
        // For Android 13+ use more specific permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        
        // For Android 10+ we need special permission for accessing all apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.QUERY_ALL_PACKAGES)
        }
        
        return permissions.toTypedArray()
    }
    
    /**
     * Check if all required permissions are granted
     * @param context the context to check
     * @return true if all permissions are granted
     */
    fun arePermissionsGranted(context: Context): Boolean {
        val requiredPermissions = getRequiredPermissions()
        
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}

/**
 * Composable function to handle permissions requests
 */
@Composable
fun PermissionHandler(
    permissions: Array<String>,
    onPermissionsResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Check if permissions are already granted
    val allPermissionsGranted = remember(permissions) {
        permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    // If all permissions are already granted, return immediately
    if (allPermissionsGranted) {
        onPermissionsResult(true)
        return
    }
    
    // Create the permission launcher if permissions need to be requested
    val permissionLauncher = rememberPermissionLauncher(
        onPermissionsResult = { granted ->
            onPermissionsResult(granted)
        }
    )
    
    // Request the permissions
    DisposableEffect(lifecycleOwner, permissions) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionLauncher.launch(permissions)
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Remember a permission launcher
 */
@Composable
private fun rememberPermissionLauncher(
    onPermissionsResult: (Boolean) -> Unit
): ActivityResultLauncher<Array<String>> {
    val context = LocalContext.current
    
    return remember {
        (context as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // All permissions granted
            val allGranted = permissions.values.all { it }
            onPermissionsResult(allGranted)
        }
    }
}