package com.multiclone.app.ui.screens.clonesetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.viewmodels.CloneSetupViewModel
import kotlinx.coroutines.launch

/**
 * Screen for configuring a new clone before creating it
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneSetupScreen(
    packageName: String,
    viewModel: CloneSetupViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onCloneCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    // Trigger load of app details when screen is shown
    LaunchedEffect(packageName) {
        viewModel.loadAppDetails(packageName)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clone Setup") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val success = viewModel.createClone()
                        if (success) {
                            onCloneCreated()
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                },
                text = { Text("Create Clone") },
                enabled = !uiState.isCreatingClone && uiState.canCreateClone
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Loading state
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } 
            // Error state
            else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = uiState.error ?: "Unknown error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(onClick = onBackClick) {
                        Text("Go Back")
                    }
                }
            } 
            // Content state
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App icon and name
                    uiState.appDetails?.let { appDetails ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            appDetails.appIcon?.let { drawable ->
                                Image(
                                    bitmap = drawable.toBitmap().asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = appDetails.appName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Text(
                            text = appDetails.packageName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Clone name input
                    OutlinedTextField(
                        value = uiState.cloneName,
                        onValueChange = { viewModel.updateCloneName(it) },
                        label = { Text("Clone Name") },
                        placeholder = { Text("My Clone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Clone configuration options
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Clone Settings",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Storage isolation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Storage Isolation",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Clone will have its own separate storage space",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Switch(
                                    checked = uiState.storageIsolated,
                                    onCheckedChange = { viewModel.toggleStorageIsolation() }
                                )
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            // Enable notifications
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Enable Notifications",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Allow clone to send notifications",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Switch(
                                    checked = uiState.notificationsEnabled,
                                    onCheckedChange = { viewModel.toggleNotifications() }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                // Creation in progress indicator
                if (uiState.isCreatingClone) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
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
                }
            }
            
            // Error snackbar
            uiState.errorMessage?.let { error ->
                SnackbarHost(
                    hostState = remember { SnackbarHostState() }.apply {
                        LaunchedEffect(error) {
                            showSnackbar(error)
                            viewModel.clearErrorMessage()
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}