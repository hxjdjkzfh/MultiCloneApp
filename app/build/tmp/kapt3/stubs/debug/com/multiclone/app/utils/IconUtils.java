package com.multiclone.app.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility class for icon-related operations
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u00042\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t\u00a8\u0006\r"}, d2 = {"Lcom/multiclone/app/utils/IconUtils;", "", "()V", "drawableToBitmap", "Landroid/graphics/Bitmap;", "drawable", "Landroid/graphics/drawable/Drawable;", "loadBitmapFromFile", "file", "Ljava/io/File;", "saveBitmapToFile", "", "bitmap", "app_debug"})
public final class IconUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.utils.IconUtils INSTANCE = null;
    
    private IconUtils() {
        super();
    }
    
    /**
     * Convert a Drawable to a Bitmap
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
     * Load a bitmap from a file
     */
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap loadBitmapFromFile(@org.jetbrains.annotations.NotNull()
    java.io.File file) {
        return null;
    }
}