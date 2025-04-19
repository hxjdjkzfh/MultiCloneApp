package com.multiclone.app.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multiclone.app.ui.theme.MultiCloneAppTheme

/**
 * Loading indicator with optional message
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null,
    color: Color = MaterialTheme.colorScheme.primary
) {
    // Create infinite transition for animation
    val transition = rememberInfiniteTransition(label = "loading")
    val scale by transition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800)
        ), label = "scale"
    )
    
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800)
        ), label = "alpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .alpha(alpha),
                color = color,
                strokeWidth = 4.dp
            )
            
            // Optional message
            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Preview of the loading indicator
 */
@Preview(showBackground = true)
@Composable
fun LoadingIndicatorPreview() {
    MultiCloneAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoadingIndicator(
                message = "Loading apps..."
            )
        }
    }
}