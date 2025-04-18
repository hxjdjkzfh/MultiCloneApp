package com.multiclone.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility functions for handling app icons and drawables
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tJ\u0018\u0010\n\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012\u00a8\u0006\u0013"}, d2 = {"Lcom/multiclone/app/utils/IconUtils;", "", "()V", "bitmapToByteArray", "", "bitmap", "Landroid/graphics/Bitmap;", "drawableToBitmap", "drawable", "Landroid/graphics/drawable/Drawable;", "getDrawable", "context", "Landroid/content/Context;", "resId", "", "saveBitmapToFile", "", "file", "Ljava/io/File;", "app_debug"})
public final class IconUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.utils.IconUtils INSTANCE = null;
    
    private IconUtils() {
        super();
    }
    
    /**
     * Convert a drawable to a bitmap
     */
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap drawableToBitmap(@org.jetbrains.annotations.NotNull()
    android.graphics.drawable.Drawable drawable) {
        return null;
    }
    
    /**
     * Save a bitmap to a file
     */
    public final boolean saveBitmapToFile(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap, @org.jetbrains.annotations.NotNull()
    java.io.File file) {
        return false;
    }
    
    /**
     * Convert a bitmap to a byte array
     */
    @org.jetbrains.annotations.NotNull()
    public final byte[] bitmapToByteArray(@org.jetbrains.annotations.NotNull()
    android.graphics.Bitmap bitmap) {
        return null;
    }
    
    /**
     * Get a drawable from a resource ID
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.drawable.Drawable getDrawable(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int resId) {
        return null;
    }
}