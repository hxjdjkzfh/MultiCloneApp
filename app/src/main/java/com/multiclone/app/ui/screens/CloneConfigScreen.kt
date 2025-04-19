package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.ui.viewmodels.AppSelectionViewModel

/**
 * Screen for configuring a new clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    appInfo: AppInfo,
    onNavigateBack: () -> Unit,
    onCloneCreated: () -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val lastCreatedClone by viewModel.lastCreatedClone.collectAsState()
    
    // State for clone name
    var showNameDialog by remember { mutableStateOf(true) }
    var cloneName by remember { mutableStateOf(appInfo.appName) }
    
    // Handle successful clone creation
    LaunchedEffect(lastCreatedClone) {
        lastCreatedClone?.let {
            // Clone was created successfully
            viewModel.clearLastCreatedClone()
            onCloneCreated()
        }
    }
    
    // Handle error
    LaunchedEffect(error) {
        error?.let {
            // In a real app, you would show a snackbar or dialog
            // For now, just reset the error after showing it
            viewModel.clearError()
        }
    }
    
    // Show dialog to enter clone name if needed
    if (showNameDialog) {
        CloneNameDialog(
            initialName = cloneName,
            onConfirm = { name ->
                cloneName = name
                showNameDialog = false
            },
            onDismiss = {
                // If user dismisses without entering a name, go back
                if (cloneName.isBlank()) {
                    onNavigateBack()
                } else {
                    showNameDialog = false
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Clone") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        // Create clone button at the bottom
        bottomBar = {
            BottomAppBar {
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        // Create the clone with current settings
                        viewModel.createClone(appInfo.packageName, cloneName)
                    },
                    enabled = !isLoading && cloneName.isNotBlank(),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Create Clone")
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Creating clone...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // Show configuration options
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App info card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App icon (placeholder)
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(bottom = 8.dp)
                        )
                        
                        // App name
                        Text(
                            text = appInfo.appName,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        
                        // Package name
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Clone name card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showNameDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clone Name",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Text(
                                text = cloneName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        IconButton(onClick = { showNameDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Edit name"
                            )
                        }
                    }
                }
                
                // Additional options can be added here
                // (Custom icon, notification settings, etc.)
            }
        }
    }
}

/**
 * Dialog for entering the clone name
 */
@Composable
fun CloneNameDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Clone Name",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                // Text field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onConfirm(name) },
                        enabled = name.isNotBlank()
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}