package com.multiclone.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object IconUtils {
    private const val TAG = "IconUtils"
    
    /**
     * Load a bitmap from a file.
     *
     * @param file The file to load the bitmap from.
     * @return The loaded bitmap, or null if loading failed.
     */
    fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from file", e)
            null
        }
    }
    
    /**
     * Save a bitmap to a file.
     *
     * @param bitmap The bitmap to save.
     * @param file The file to save the bitmap to.
     * @return True if the bitmap was saved successfully, false otherwise.
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to file", e)
            false
        }
    }
    
    /**
     * Load a bitmap from a content URI.
     *
     * @param uri The URI to load the bitmap from.
     * @param context The application context.
     * @return The loaded bitmap, or null if loading failed.
     */
    fun loadBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            null
        }
    }
    
    /**
     * Create an adaptive icon badge for the shortcut.
     *
     * @param icon The icon bitmap to use.
     * @return A new bitmap with the adaptive icon badge.
     */
    fun createIconAdaptiveBadge(icon: Bitmap): Bitmap {
        val size = Math.max(icon.width, icon.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        // Scale the icon to fit
        val matrix = Matrix()
        val scale = size.toFloat() / Math.min(icon.width, icon.height)
        matrix.setScale(scale, scale)
        
        // Center the icon
        val dx = (size - icon.width * scale) / 2f
        val dy = (size - icon.height * scale) / 2f
        matrix.postTranslate(dx, dy)
        
        // Create a rounded rect for the icon
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawRoundRect(rect, size / 5f, size / 5f, paint)
        
        // Apply mask
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(icon, matrix, paint)
        
        return output
    }
    
    /**
     * Resize a bitmap to a specific size.
     *
     * @param bitmap The bitmap to resize.
     * @param width The target width.
     * @param height The target height.
     * @return The resized bitmap.
     */
    fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val scaleWidth = width.toFloat() / bitmap.width
        val scaleHeight = height.toFloat() / bitmap.height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
