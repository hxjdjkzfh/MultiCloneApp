package com.multiclone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the application, following Material 3 guidelines.
 */
val Shapes = Shapes(
    // Small components like buttons, chips
    small = RoundedCornerShape(4.dp),
    
    // Medium components like cards, alert dialogs
    medium = RoundedCornerShape(8.dp),
    
    // Large components like sheets, expanded dialogs
    large = RoundedCornerShape(12.dp),
    
    // Extra large components
    extraLarge = RoundedCornerShape(16.dp)
)