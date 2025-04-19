package com.multiclone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Custom shapes for the application
 * Material 3 has default shapes, but we're customizing them slightly
 */
val Shapes = Shapes(
    // Small components like small buttons, chips, etc.
    small = RoundedCornerShape(8.dp),
    
    // Medium components like cards, dialog surfaces, etc.
    medium = RoundedCornerShape(12.dp),
    
    // Large components like bottom sheets, expanded menu surfaces, etc.
    large = RoundedCornerShape(16.dp),
    
    // Extra large components - used for custom components
    extraLarge = RoundedCornerShape(24.dp)
)