package com.multiclone.app.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File
import java.io.FileOutputStream

/**
 * Utility class for icon-related operations
 */
object IconUtils {
    
    /**
     * Convert a Drawable to a Bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        
        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    
    /**
     * Save a bitmap to a file
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Load a bitmap from a file
     */
    fun loadBitmapFromFile(file: File): Bitmap? {
        return if (file.exists()) {
            try {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}