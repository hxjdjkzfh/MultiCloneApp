package com.multiclone.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.compose.runtime.Composable;
import androidx.core.content.ContextCompat;
import com.google.accompanist.permissions.ExperimentalPermissionsApi;
import com.google.accompanist.permissions.MultiplePermissionsState;
import com.google.accompanist.permissions.PermissionState;

/**
 * Utility functions for handling app permissions
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\u0016\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\tJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u000f"}, d2 = {"Lcom/multiclone/app/utils/PermissionUtils;", "", "()V", "areAllPermissionsGranted", "", "context", "Landroid/content/Context;", "permissions", "", "", "getRequiredPermissions", "isPermissionGranted", "permission", "openAppSettings", "", "app_debug"})
public final class PermissionUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.utils.PermissionUtils INSTANCE = null;
    
    private PermissionUtils() {
        super();
    }
    
    /**
     * Check if a single permission is granted
     */
    public final boolean isPermissionGranted(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String permission) {
        return false;
    }
    
    /**
     * Check if all permissions in a list are granted
     */
    public final boolean areAllPermissionsGranted(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> permissions) {
        return false;
    }
    
    /**
     * Get the list of required permissions for the app based on Android version
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getRequiredPermissions() {
        return null;
    }
    
    /**
     * Open the app settings screen
     */
    public final void openAppSettings(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
}