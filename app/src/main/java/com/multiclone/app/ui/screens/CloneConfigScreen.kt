package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.viewmodels.CloneConfigViewModel

/**
 * Screen for configuring a new app clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    packageName: String,
    onBackClick: () -> Unit,
    onCloneCreated: (String) -> Unit,
    viewModel: CloneConfigViewModel = hiltViewModel()
) {
    val appInfo by viewModel.appInfo.collectAsState()
    val cloneName by viewModel.cloneName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val createdCloneId by viewModel.createdCloneId.collectAsState()
    
    var notificationsEnabled by remember { mutableStateOf(true) }
    
    // Load app info when the screen is first composed
    LaunchedEffect(packageName) {
        viewModel.loadAppInfo(packageName)
    }
    
    // Navigate back when clone is created
    LaunchedEffect(createdCloneId) {
        createdCloneId?.let {
            onCloneCreated(it)
            viewModel.resetCreatedCloneId()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Clone") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createClone() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                enabled = !isLoading && appInfo != null && cloneName.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Create Clone"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                appInfo?.let { app ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App icon
                        Surface(
                            modifier = Modifier.size(96.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                bitmap = app.appIcon.toBitmap().asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // App name
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Clone name field
                        OutlinedTextField(
                            value = cloneName,
                            onValueChange = { viewModel.updateCloneName(it) },
                            label = { Text("Clone Name") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Notifications toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (notificationsEnabled) 
                                    Icons.Default.Notifications 
                                else 
                                    Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Enable Notifications",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp)
                            )
                            
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                        
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        
                        // Information card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Clone Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "This will create an isolated version of ${app.appName} that runs in its own environment. You can run multiple instances of the same app with different accounts.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                } ?: run {
                    // Show message if app info could not be loaded
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Could not load app information",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(onClick = onBackClick) {
                            Text("Go Back")
                        }
                    }
                }
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