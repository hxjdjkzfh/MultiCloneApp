package com.multiclone.app.domain.virtualization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.data.model.CloneInfo;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Core engine that manages virtualization and app cloning
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJL\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\n2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u0019\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rH\u0086@\u00f8\u0001\u0002\u00a2\u0006\u0002\u0010\u0019J\u0019\u0010\u001a\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rH\u0086@\u00f8\u0001\u0002\u00a2\u0006\u0002\u0010\u0019J\u0010\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u001b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010\u000e\u001a\u00020\nH\u0082@\u00f8\u0001\u0002\u00a2\u0006\u0002\u0010 J\u0017\u0010!\u001a\b\u0012\u0004\u0012\u00020\u001f0\"H\u0086@\u00f8\u0001\u0002\u00a2\u0006\u0002\u0010#J\u0019\u0010$\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\rH\u0086@\u00f8\u0001\u0002\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\t\u001a\u00020\nX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000f\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b\u0019\u00a8\u0006%"}, d2 = {"Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "", "context", "Landroid/content/Context;", "cloneEnvironment", "Lcom/multiclone/app/domain/virtualization/CloneEnvironment;", "clonedAppInstaller", "Lcom/multiclone/app/domain/virtualization/ClonedAppInstaller;", "(Landroid/content/Context;Lcom/multiclone/app/domain/virtualization/CloneEnvironment;Lcom/multiclone/app/domain/virtualization/ClonedAppInstaller;)V", "TAG", "", "createClone", "Lkotlin/Result;", "Lcom/multiclone/app/data/model/CloneInfo;", "packageName", "customName", "customIcon", "Landroid/graphics/Bitmap;", "cloneIndex", "", "createClone-yxL6bBk", "(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createShortcut", "", "cloneInfo", "(Lcom/multiclone/app/data/model/CloneInfo;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteClone", "drawableToBitmap", "drawable", "Landroid/graphics/drawable/Drawable;", "getAppInfo", "Lcom/multiclone/app/data/model/AppInfo;", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getInstalledApps", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "launchClone", "app_debug"})
@javax.inject.Singleton()
public final class VirtualAppEngine {
    private final android.content.Context context = null;
    private final com.multiclone.app.domain.virtualization.CloneEnvironment cloneEnvironment = null;
    private final com.multiclone.app.domain.virtualization.ClonedAppInstaller clonedAppInstaller = null;
    private final java.lang.String TAG = "VirtualAppEngine";
    
    @javax.inject.Inject()
    public VirtualAppEngine(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.CloneEnvironment cloneEnvironment, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.ClonedAppInstaller clonedAppInstaller) {
        super();
    }
    
    /**
     * Get all installed applications on the device
     * Filters out system apps and the current app to prevent self-cloning issues
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getInstalledApps(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.multiclone.app.data.model.AppInfo>> continuation) {
        return null;
    }
    
    /**
     * Launch a cloned app
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object launchClone(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.CloneInfo cloneInfo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> continuation) {
        return null;
    }
    
    /**
     * Create a shortcut for a cloned app
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createShortcut(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.CloneInfo cloneInfo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> continuation) {
        return null;
    }
    
    /**
     * Delete a cloned app
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteClone(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.CloneInfo cloneInfo, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> continuation) {
        return null;
    }
    
    /**
     * Get information about an installed app
     */
    private final java.lang.Object getAppInfo(java.lang.String packageName, kotlin.coroutines.Continuation<? super com.multiclone.app.data.model.AppInfo> continuation) {
        return null;
    }
    
    /**
     * Convert a drawable to a bitmap
     */
    private final android.graphics.Bitmap drawableToBitmap(android.graphics.drawable.Drawable drawable) {
        return null;
    }
}