package com.multiclone.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multiclone.app.R
import com.multiclone.app.data.model.CloneInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * List of cloned apps.
 * 
 * @param clones List of clones to display
 * @param onCloneClick Callback when a clone is clicked
 * @param onCloneEdit Callback when edit button is clicked
 * @param onCloneDelete Callback when delete button is clicked
 * @param modifier Modifier for customizing the component
 */
@Composable
fun CloneList(
    clones: List<CloneInfo>,
    onCloneClick: (String) -> Unit,
    onCloneEdit: (String) -> Unit,
    onCloneDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = clones,
            key = { it.id }
        ) { clone ->
            CloneItem(
                clone = clone,
                onClick = { onCloneClick(clone.id) },
                onEdit = { onCloneEdit(clone.id) },
                onDelete = { onCloneDelete(clone.id) }
            )
        }
    }
}

/**
 * Individual clone item.
 * 
 * @param clone Clone to display
 * @param onClick Callback when the item is clicked
 * @param onEdit Callback when edit button is clicked
 * @param onDelete Callback when delete button is clicked
 * @param modifier Modifier for customizing the component
 */
@Composable
fun CloneItem(
    clone: CloneInfo,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clone color indicator
            val cloneColor = try {
                Color(android.graphics.Color.parseColor(clone.color))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(cloneColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = clone.customName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Clone details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = clone.customName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Show last launched time if available
                if (clone.lastLaunchedAt > 0) {
                    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    val lastLaunchedDate = dateFormat.format(Date(clone.lastLaunchedAt))
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Last used: $lastLaunchedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Show launch count if available
                if (clone.launchCount > 0) {
                    Text(
                        text = "Used ${clone.launchCount} ${if (clone.launchCount == 1) "time" else "times"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}