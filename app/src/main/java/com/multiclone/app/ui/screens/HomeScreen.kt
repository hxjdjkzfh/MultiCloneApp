package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home screen displaying list of app clones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddCloneClick: () -> Unit,
    onCloneClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val clones by viewModel.clones.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiClone App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCloneClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Clone")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (clones.isEmpty()) {
                EmptyClonesList(
                    onAddCloneClick = onAddCloneClick,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ClonesList(
                    clones = clones,
                    onCloneClick = onCloneClick,
                    onDeleteClone = { viewModel.deleteClone(it) }
                )
            }
            
            // Show error snackbar if any
            error?.let {
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}

/**
 * List of clones
 */
@Composable
fun ClonesList(
    clones: List<CloneInfo>,
    onCloneClick: (String) -> Unit,
    onDeleteClone: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(clones) { clone ->
            CloneItem(
                clone = clone,
                onClick = { onCloneClick(clone.id) },
                onDelete = { onDeleteClone(clone.id) }
            )
        }
    }
}

/**
 * Individual clone item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneItem(
    clone: CloneInfo,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            
            // Clone info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = clone.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = clone.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Last used: ${dateFormatter.format(clone.lastUsedDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Empty state when no clones are available
 */
@Composable
fun EmptyClonesList(
    onAddCloneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No App Clones Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Create your first app clone to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddCloneClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Clone")
        }
    }
}