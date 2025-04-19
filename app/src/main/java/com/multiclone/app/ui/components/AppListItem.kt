package com.multiclone.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.multiclone.app.data.model.AppInfo

/**
 * Composable for displaying an app item in a list
 */
@Composable
fun AppListItem(
    appInfo: AppInfo,
    selectable: Boolean = false,
    onClick: (AppInfo) -> Unit = {},
    onLongClick: (AppInfo) -> Unit = {},
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(appInfo) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                appInfo.icon?.let { icon ->
                    Image(
                        bitmap = icon.toBitmap().asImageBitmap(),
                        contentDescription = "${appInfo.appName} icon",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // App details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${appInfo.getVersionString()} • ${appInfo.getSizeString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Checkbox for selection
            if (selectable) {
                Checkbox(
                    checked = appInfo.isSelected,
                    onCheckedChange = { checked ->
                        onCheckedChange?.invoke(checked)
                    }
                )
            }
        }
    }
}