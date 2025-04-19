package com.multiclone.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import com.multiclone.app.data.model.AppInfo

/**
 * List item for displaying an installed app
 */
@Composable
fun AppListItem(
    appInfo: AppInfo,
    onClick: (AppInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingContent = {
                // In a real app, we would load the app's icon from appInfo.iconUri
                // For this demo, we'll use a simple colored box
                appInfo.appIcon?.let { icon ->
                    Image(
                        bitmap = icon,
                        contentDescription = "${appInfo.appName} icon",
                        modifier = Modifier.size(48.dp)
                    )
                } ?: Image(
                    painter = ColorPainter(MaterialTheme.colorScheme.primary),
                    contentDescription = "${appInfo.appName} icon",
                    modifier = Modifier.size(48.dp)
                )
            },
            modifier = Modifier.clickable { onClick(appInfo) }
        )
    }
}