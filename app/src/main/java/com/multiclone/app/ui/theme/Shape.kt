package com.multiclone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for the MultiClone app following Material3 guidelines
 */
val Shapes = Shapes(
    // Used for small components like chips, small buttons
    small = RoundedCornerShape(4.dp),
    
    // Used for medium components like cards, dialogs, etc.
    medium = RoundedCornerShape(8.dp),
    
    // Used for large components like bottom sheets, large dialogs
    large = RoundedCornerShape(16.dp),
    
    // Used for extra large components like top-level surfaces
    extraLarge = RoundedCornerShape(24.dp)
)