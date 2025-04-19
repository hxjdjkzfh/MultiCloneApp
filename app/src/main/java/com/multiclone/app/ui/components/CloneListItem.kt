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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.multiclone.app.data.model.CloneInfo

/**
 * Composable for displaying a cloned app in a list
 */
@Composable
fun CloneListItem(
    cloneInfo: CloneInfo,
    onLaunchClick: (CloneInfo) -> Unit,
    onInfoClick: (CloneInfo) -> Unit,
    onSettingsClick: (CloneInfo) -> Unit,
    onDeleteClick: (CloneInfo) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onLaunchClick(cloneInfo) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (cloneInfo.isRunning) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                cloneInfo.icon?.let { icon ->
                    BadgedBox(
                        badge = {
                            if (cloneInfo.isRunning) {
                                Badge {
                                    Text("●")
                                }
                            }
                        }
                    ) {
                        Image(
                            bitmap = icon.toBitmap().asImageBitmap(),
                            contentDescription = "${cloneInfo.cloneName} icon",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Clone details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cloneInfo.cloneName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = cloneInfo.originalPackageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Launched ${cloneInfo.launchCount} times",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Launch button
            IconButton(onClick = { onLaunchClick(cloneInfo) }) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = "Launch",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // More options
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Info") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            onInfoClick(cloneInfo)
                            showMenu = false
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            onSettingsClick(cloneInfo)
                            showMenu = false
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            onDeleteClick(cloneInfo)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}