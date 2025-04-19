package com.multiclone.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for MultiClone app components.
 * Follows Material 3 design guidelines for component shapes.
 */
val Shapes = Shapes(
    // Shapes for small components like buttons, text fields
    small = RoundedCornerShape(4.dp),
    
    // Shapes for medium-sized components like cards
    medium = RoundedCornerShape(8.dp),
    
    // Shapes for large components like bottom sheets
    large = RoundedCornerShape(12.dp),
    
    // Shape for extra large components
    extraLarge = RoundedCornerShape(16.dp)
)