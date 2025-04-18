package com.multiclone.app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Utility class for handling app icons
 */
@Singleton
class IconUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager = context.packageManager
    
    /**
     * Get an app's icon as a Bitmap
     */
    suspend fun getAppIcon(packageName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val drawable = packageManager.getApplicationIcon(appInfo)
            drawableToBitmap(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Draw a badge on an icon
     * @param icon The original icon bitmap
     * @param badge The badge text to draw
     */
    fun drawBadge(icon: Bitmap, badge: String): Bitmap {
        val badgeSize = icon.width / 2.5f
        val badgePosition = RectF(
            icon.width - badgeSize,
            icon.height - badgeSize,
            icon.width.toFloat(),
            icon.height.toFloat()
        )
        
        val result = Bitmap.createBitmap(icon.width, icon.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        
        // Draw the original icon
        canvas.drawBitmap(icon, 0f, 0f, null)
        
        // Draw badge background
        val bgPaint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
        }
        canvas.drawOval(badgePosition, bgPaint)
        
        // Draw badge text
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = badgeSize * 0.6f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        val textBounds = Rect()
        textPaint.getTextBounds(badge, 0, badge.length, textBounds)
        
        val textX = badgePosition.centerX()
        val textY = badgePosition.centerY() + (textBounds.height() / 2)
        
        canvas.drawText(badge, textX, textY, textPaint)
        
        return result
    }
    
    /**
     * Create a circular icon from an app icon
     */
    fun createCircularIcon(icon: Bitmap, size: Int): Bitmap {
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
        
        val rect = Rect(0, 0, size, size)
        val rectF = RectF(rect)
        
        canvas.drawOval(rectF, paint)
        
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        
        // Scale the original icon to fit
        val scaledIcon = Bitmap.createScaledBitmap(icon, size, size, true)
        canvas.drawBitmap(scaledIcon, rect, rect, paint)
        
        return output
    }
    
    /**
     * Convert a drawable to a bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        
        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 128
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 128
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        
        return bitmap
    }
}