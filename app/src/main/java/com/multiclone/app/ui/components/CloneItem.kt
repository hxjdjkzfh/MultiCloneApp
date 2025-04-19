package com.multiclone.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.theme.CloneBadge
import com.multiclone.app.ui.theme.CloneCardBackground
import com.multiclone.app.ui.theme.MultiCloneTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * A reusable composable component that displays a cloned app 
 * in the home screen with actions to launch, edit, or delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneItem(
    clone: CloneInfo,
    onLaunchClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = CloneCardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // App info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Icon with Badge
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    clone.originalAppIcon?.let { drawable ->
                        Image(
                            bitmap = drawable.toBitmap().asImageBitmap(),
                            contentDescription = "Icon for ${clone.originalAppName}",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    } ?: Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    
                    // Badge
                    Surface(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(1.dp),
                        shape = MaterialTheme.shapes.small,
                        color = clone.badgeColor ?: CloneBadge,
                        content = {}
                    )
                }
                
                // Clone info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = clone.getDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "Original: ${clone.originalAppName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Usage stats
                    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    val lastUsed = if (clone.lastLaunchTime > 0) {
                        "Last used: ${dateFormat.format(Date(clone.lastLaunchTime))}"
                    } else {
                        "Never launched"
                    }
                    
                    Text(
                        text = "$lastUsed • ${clone.launchCount} launches",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Launch button
                ElevatedButton(
                    onClick = onLaunchClick,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Launch",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Launch")
                }
                
                // Edit button
                OutlinedButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit")
                }
                
                // Delete button
                OutlinedButton(
                    onClick = { showDeleteDialog.value = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Delete")
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Clone") },
            text = { Text("Are you sure you want to delete the clone '${clone.getDisplayName()}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog.value = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CloneItemPreview() {
    MultiCloneTheme {
        CloneItem(
            clone = CloneInfo(
                id = "12345",
                packageName = "com.example.app",
                originalAppName = "Example App",
                cloneName = "Work Profile",
                creationTime = System.currentTimeMillis(),
                lastLaunchTime = System.currentTimeMillis() - 86400000, // 1 day ago
                launchCount = 5
            ),
            onLaunchClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}