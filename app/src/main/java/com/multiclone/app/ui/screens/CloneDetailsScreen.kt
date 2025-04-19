package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.viewmodels.CloneDetailsViewModel
import com.multiclone.app.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

/**
 * Screen to view and edit clone details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneDetailsScreen(
    cloneId: String,
    onNavigateBack: () -> Unit,
    onLaunchClone: (String, String) -> Unit,
    onDeleteClone: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val clones by viewModel.clones.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()
    
    // Find the clone by ID
    val clone = remember(clones, cloneId) {
        clones.find { it.id == cloneId }
    }
    
    // Delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show delete confirmation dialog
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                // Delete the clone
                viewModel.deleteClone(cloneId)
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(clone?.displayName ?: "Clone Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Delete button
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete clone"
                        )
                    }
                }
            )
        },
        // Launch button at the bottom
        bottomBar = {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        clone?.let {
                            onLaunchClone(it.packageName, it.id)
                        }
                    },
                    enabled = clone != null && !isLoading,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Launch Clone")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (clone == null) {
            // Clone not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Clone not found",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Clone details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Clone info card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Clone icon (placeholder)
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 8.dp)
                        )
                        
                        // Clone name
                        Text(
                            text = clone.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        
                        // Package name
                        Text(
                            text = clone.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Notifications setting
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notifications",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Text(
                                text = if (clone.notificationsEnabled) "Enabled" else "Disabled",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Switch(
                            checked = clone.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                // Update notifications setting
                                scope.launch {
                                    viewModel.updateNotificationSettings(clone.id, enabled)
                                    
                                    // Show confirmation
                                    snackbarHostState.showSnackbar(
                                        message = "Notifications ${if (enabled) "enabled" else "disabled"}"
                                    )
                                }
                            }
                        )
                    }
                }
                
                // Clone creation time
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Created",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            // Format date - Note: In a real app you would use a proper date formatter
                            Text(
                                text = java.util.Date(clone.creationTime).toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Last used time
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Last Used",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            // Format date - Note: In a real app you would use a proper date formatter
                            Text(
                                text = java.util.Date(clone.lastUsedTime).toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to confirm clone deletion
 */
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Clone") },
        text = { Text("Are you sure you want to delete this clone? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}