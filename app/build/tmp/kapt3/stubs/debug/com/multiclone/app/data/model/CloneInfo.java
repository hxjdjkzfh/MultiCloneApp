package com.multiclone.app.data.model;

import android.graphics.Bitmap;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize;
import kotlinx.parcelize.RawValue;

/**
 * Data model representing a cloned application
 */
@kotlinx.parcelize.Parcelize()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0019\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BL\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\r\u0010\u0007\u001a\t\u0018\u00010\b\u00a2\u0006\u0002\b\t\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010 \u001a\t\u0018\u00010\b\u00a2\u0006\u0002\b\tH\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\fH\u00c6\u0003J\t\u0010#\u001a\u00020\fH\u00c6\u0003J`\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u000f\b\u0002\u0010\u0007\u001a\t\u0018\u00010\b\u00a2\u0006\u0002\b\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u00c6\u0001J\t\u0010%\u001a\u00020&H\u00d6\u0001J\u0013\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010*H\u00d6\u0003J\t\u0010+\u001a\u00020&H\u00d6\u0001J\t\u0010,\u001a\u00020\u0003H\u00d6\u0001J\u0019\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u0002002\u0006\u00101\u001a\u00020&H\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0018\u0010\u0007\u001a\t\u0018\u00010\b\u00a2\u0006\u0002\b\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u001a\u0010\r\u001a\u00020\fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0010\"\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014\u00a8\u00062"}, d2 = {"Lcom/multiclone/app/data/model/CloneInfo;", "Landroid/os/Parcelable;", "id", "", "packageName", "originalAppName", "displayName", "customIcon", "Landroid/graphics/Bitmap;", "Lkotlinx/parcelize/RawValue;", "virtualEnvironmentId", "creationTimestamp", "", "lastUsedTimestamp", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;Ljava/lang/String;JJ)V", "getCreationTimestamp", "()J", "getCustomIcon", "()Landroid/graphics/Bitmap;", "getDisplayName", "()Ljava/lang/String;", "getId", "getLastUsedTimestamp", "setLastUsedTimestamp", "(J)V", "getOriginalAppName", "getPackageName", "getVirtualEnvironmentId", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "describeContents", "", "equals", "", "other", "", "hashCode", "toString", "writeToParcel", "", "parcel", "Landroid/os/Parcel;", "flags", "app_debug"})
public final class CloneInfo implements android.os.Parcelable {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String packageName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String originalAppName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayName = null;
    @org.jetbrains.annotations.Nullable()
    private final android.graphics.Bitmap customIcon = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String virtualEnvironmentId = null;
    private final long creationTimestamp = 0L;
    private long lastUsedTimestamp;
    public static final android.os.Parcelable.Creator<com.multiclone.app.data.model.CloneInfo> CREATOR = null;
    
    /**
     * Data model representing a cloned application
     */
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.data.model.CloneInfo copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String originalAppName, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap customIcon, @org.jetbrains.annotations.NotNull()
    java.lang.String virtualEnvironmentId, long creationTimestamp, long lastUsedTimestamp) {
        return null;
    }
    
    /**
     * Data model representing a cloned application
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Data model representing a cloned application
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Data model representing a cloned application
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public CloneInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String originalAppName, @org.jetbrains.annotations.NotNull()
    java.lang.String displayName, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap customIcon, @org.jetbrains.annotations.NotNull()
    java.lang.String virtualEnvironmentId, long creationTimestamp, long lastUsedTimestamp) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPackageName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOriginalAppName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap getCustomIcon() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getVirtualEnvironmentId() {
        return null;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long getCreationTimestamp() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long getLastUsedTimestamp() {
        return 0L;
    }
    
    public final void setLastUsedTimestamp(long p0) {
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
    public static final class Creator implements android.os.Parcelable.Creator<com.multiclone.app.data.model.CloneInfo> {
        
        public Creator() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public final com.multiclone.app.data.model.CloneInfo createFromParcel(@org.jetbrains.annotations.NotNull()
        android.os.Parcel in) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public final com.multiclone.app.data.model.CloneInfo[] newArray(int size) {
            return null;
        }
    }
}