package com.multiclone.app.data.model;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize;
import kotlinx.parcelize.RawValue;

/**
 * Data model representing an installed application
 */
@kotlinx.parcelize.Parcelize()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B4\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u000b\u0010\u0006\u001a\u00070\u0007\u00a2\u0006\u0002\b\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000e\u0010\u0016\u001a\u00070\u0007\u00a2\u0006\u0002\b\bH\u00c6\u0003J\t\u0010\u0017\u001a\u00020\nH\u00c6\u0003JB\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\r\b\u0002\u0010\u0006\u001a\u00070\u0007\u00a2\u0006\u0002\b\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\u0013\u0010\u001b\u001a\u00020\n2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\u0003H\u00d6\u0001J\u0019\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u001aH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00070\u0007\u00a2\u0006\u0002\b\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\r\u00a8\u0006%"}, d2 = {"Lcom/multiclone/app/data/model/AppInfo;", "Landroid/os/Parcelable;", "packageName", "", "appName", "versionName", "icon", "Landroid/graphics/drawable/Drawable;", "Lkotlinx/parcelize/RawValue;", "isSystemApp", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Z)V", "getAppName", "()Ljava/lang/String;", "getIcon", "()Landroid/graphics/drawable/Drawable;", "()Z", "getPackageName", "getVersionName", "component1", "component2", "component3", "component4", "component5", "copy", "describeContents", "", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", "parcel", "Landroid/os/Parcel;", "flags", "app_debug"})
public final class AppInfo implements android.os.Parcelable {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String packageName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String appName = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String versionName = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.drawable.Drawable icon = null;
    private final boolean isSystemApp = false;
    public static final android.os.Parcelable.Creator<com.multiclone.app.data.model.AppInfo> CREATOR = null;
    
    /**
     * Data model representing an installed application
     */
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.data.model.AppInfo copy(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String appName, @org.jetbrains.annotations.Nullable()
    java.lang.String versionName, @org.jetbrains.annotations.NotNull()
    android.graphics.drawable.Drawable icon, boolean isSystemApp) {
        return null;
    }
    
    /**
     * Data model representing an installed application
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Data model representing an installed application
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Data model representing an installed application
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public AppInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String appName, @org.jetbrains.annotations.Nullable()
    java.lang.String versionName, @org.jetbrains.annotations.NotNull()
    android.graphics.drawable.Drawable icon, boolean isSystemApp) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPackageName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAppName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getVersionName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.drawable.Drawable component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.drawable.Drawable getIcon() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    public final boolean isSystemApp() {
        return false;
    }
    
    @java.lang.Override()
    public int describeContents() {
        return 0;
    }
    
    @java.lang.Override()
    public void writeToParcel(@org.jetbrains.annotations.NotNull()
    android.os.Parcel parcel, int flags) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 3)
    public static final class Creator implements android.os.Parcelable.Creator<com.multiclone.app.data.model.AppInfo> {
        
        public Creator() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public final com.multiclone.app.data.model.AppInfo createFromParcel(@org.jetbrains.annotations.NotNull()
        android.os.Parcel in) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public final com.multiclone.app.data.model.AppInfo[] newArray(int size) {
            return null;
        }
    }
}