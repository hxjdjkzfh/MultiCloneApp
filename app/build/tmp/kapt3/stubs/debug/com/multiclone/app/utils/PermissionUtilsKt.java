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

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\u001a*\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u0006H\u0007\u00a8\u0006\b"}, d2 = {"PermissionHandler", "", "permissions", "", "", "onPermissionsResult", "Lkotlin/Function1;", "", "app_debug"})
public final class PermissionUtilsKt {
    
    /**
     * Composable function to handle permissions request
     */
    @androidx.compose.runtime.Composable()
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    public static final void PermissionHandler(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> permissions, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPermissionsResult) {
    }
}