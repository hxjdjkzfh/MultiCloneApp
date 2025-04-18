package com.multiclone.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.runtime.Composable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a/\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u00a2\u0006\u0002\u0010\b\u001a(\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\n2\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u00a8\u0006\u000b"}, d2 = {"PermissionHandler", "", "permissions", "", "", "onPermissionsResult", "Lkotlin/Function1;", "", "([Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "rememberPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "app_debug"})
public final class PermissionUtilsKt {
    
    /**
     * Composable function to handle permissions requests
     */
    @androidx.compose.runtime.Composable()
    public static final void PermissionHandler(@org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPermissionsResult) {
    }
    
    /**
     * Remember a permission launcher
     */
    @androidx.compose.runtime.Composable()
    private static final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> rememberPermissionLauncher(kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPermissionsResult) {
        return null;
    }
}