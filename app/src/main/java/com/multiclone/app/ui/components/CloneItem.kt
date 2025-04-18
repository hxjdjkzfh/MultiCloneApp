package com.multiclone.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multiclone.app.data.model.CloneInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A composable for displaying a clone item in a list
 */
@Composable
fun CloneItem(
    cloneInfo: CloneInfo,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .let { modifier ->
                if (onLongClick != null) {
                    modifier.clickable(
                        onClick = {},
                        onLongClick = onLongClick
                    )
                } else {
                    modifier
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            cloneInfo.customIcon?.let { icon ->
                Image(
                    bitmap = icon.asImageBitmap(),
                    contentDescription = "Clone icon",
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Clone details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Display name
                Text(
                    text = cloneInfo.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Original app name if custom name is used
                if (cloneInfo.displayName != cloneInfo.originalAppName) {
                    Text(
                        text = cloneInfo.originalAppName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Last used info
                val formattedDate = formatDate(cloneInfo.lastUsedTimestamp)
                Text(
                    text = "Last used: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Format a timestamp into a readable date
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    return format.format(date)
}