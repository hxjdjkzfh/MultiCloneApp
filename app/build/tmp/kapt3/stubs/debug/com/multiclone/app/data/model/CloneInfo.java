package com.multiclone.app.data.model;

import android.graphics.Bitmap;
import android.os.Parcelable;
import kotlinx.parcelize.Parcelize;
import java.util.UUID;

/**
 * Data class representing information about a cloned app
 *
 * @property id Unique identifier for the clone
 * @property packageName The package name of the original app
 * @property originalAppName The display name of the original app
 * @property customName The custom name for the clone (optional)
 * @property icon The custom icon bitmap for the clone (optional)
 * @property cloneIndex The index of this clone (for multiple clones of the same app)
 * @property createdAt Timestamp when this clone was created
 * @property lastUsedAt Timestamp when this clone was last used
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001BU\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010!\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010\"\u001a\u00020\nH\u00c6\u0003J\t\u0010#\u001a\u00020\fH\u00c6\u0003J\t\u0010$\u001a\u00020\fH\u00c6\u0003J]\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u00c6\u0001J\u0013\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010)H\u00d6\u0003J\t\u0010*\u001a\u00020\nH\u00d6\u0001J\t\u0010+\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0014R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014\u00a8\u0006,"}, d2 = {"Lcom/multiclone/app/data/model/CloneInfo;", "Landroid/os/Parcelable;", "id", "", "packageName", "originalAppName", "customName", "icon", "Landroid/graphics/Bitmap;", "cloneIndex", "", "createdAt", "", "lastUsedAt", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/Bitmap;IJJ)V", "getCloneIndex", "()I", "getCreatedAt", "()J", "getCustomName", "()Ljava/lang/String;", "displayName", "getDisplayName", "getIcon", "()Landroid/graphics/Bitmap;", "getId", "getLastUsedAt", "getOriginalAppName", "getPackageName", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "", "hashCode", "toString", "app_debug"})
@Parcelize()
public final class CloneInfo implements android.os.Parcelable {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String packageName = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String originalAppName = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String customName = null;
    @org.jetbrains.annotations.Nullable()
    private final android.graphics.Bitmap icon = null;
    private final int cloneIndex = 0;
    private final long createdAt = 0L;
    private final long lastUsedAt = 0L;
    
    /**
     * Data class representing information about a cloned app
     *
     * @property id Unique identifier for the clone
     * @property packageName The package name of the original app
     * @property originalAppName The display name of the original app
     * @property customName The custom name for the clone (optional)
     * @property icon The custom icon bitmap for the clone (optional)
     * @property cloneIndex The index of this clone (for multiple clones of the same app)
     * @property createdAt Timestamp when this clone was created
     * @property lastUsedAt Timestamp when this clone was last used
     */
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.data.model.CloneInfo copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String originalAppName, @org.jetbrains.annotations.Nullable()
    java.lang.String customName, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap icon, int cloneIndex, long createdAt, long lastUsedAt) {
        return null;
    }
    
    /**
     * Data class representing information about a cloned app
     *
     * @property id Unique identifier for the clone
     * @property packageName The package name of the original app
     * @property originalAppName The display name of the original app
     * @property customName The custom name for the clone (optional)
     * @property icon The custom icon bitmap for the clone (optional)
     * @property cloneIndex The index of this clone (for multiple clones of the same app)
     * @property createdAt Timestamp when this clone was created
     * @property lastUsedAt Timestamp when this clone was last used
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Data class representing information about a cloned app
     *
     * @property id Unique identifier for the clone
     * @property packageName The package name of the original app
     * @property originalAppName The display name of the original app
     * @property customName The custom name for the clone (optional)
     * @property icon The custom icon bitmap for the clone (optional)
     * @property cloneIndex The index of this clone (for multiple clones of the same app)
     * @property createdAt Timestamp when this clone was created
     * @property lastUsedAt Timestamp when this clone was last used
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Data class representing information about a cloned app
     *
     * @property id Unique identifier for the clone
     * @property packageName The package name of the original app
     * @property originalAppName The display name of the original app
     * @property customName The custom name for the clone (optional)
     * @property icon The custom icon bitmap for the clone (optional)
     * @property cloneIndex The index of this clone (for multiple clones of the same app)
     * @property createdAt Timestamp when this clone was created
     * @property lastUsedAt Timestamp when this clone was last used
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public CloneInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String originalAppName, @org.jetbrains.annotations.Nullable()
    java.lang.String customName, @org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap icon, int cloneIndex, long createdAt, long lastUsedAt) {
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
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCustomName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap getIcon() {
        return null;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final int getCloneIndex() {
        return 0;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long getCreatedAt() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long getLastUsedAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayName() {
        return null;
    }
}