package com.multiclone.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.utils.IconUtils
import com.multiclone.app.viewmodel.CloneConfigViewModel

/**
 * Screen for configuring a new clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    onBackPressed: () -> Unit,
    onCloneCreated: (CloneInfo) -> Unit,
    viewModel: CloneConfigViewModel = hiltViewModel()
) {
    val selectedApp by viewModel.selectedApp.collectAsState()
    val displayName by viewModel.displayName.collectAsState()
    val customIcon by viewModel.customIcon.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cloneCreated by viewModel.cloneCreated.collectAsState()
    
    // Handle navigation when clone is created
    LaunchedEffect(cloneCreated) {
        cloneCreated?.let {
            onCloneCreated(it)
            viewModel.resetCloneCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Clone") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createClone() },
                content = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Create Clone"
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            selectedApp?.let { app ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App icon
                    val iconBitmap = customIcon ?: IconUtils.drawableToBitmap(app.icon)
                    Image(
                        bitmap = iconBitmap.asImageBitmap(),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(8.dp)
                            .clickable {
                                // In a real app, this would open an icon picker
                            }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Original app name
                    Text(
                        text = "Original App: ${app.appName}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Display name field
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { viewModel.updateDisplayName(it) },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "The clone will be created with a separate data storage and can be used independently from the original app.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.createClone() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Clone")
                    }
                }
            } ?: run {
                // App not found view
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "App not found",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            
            // Loading overlay
            LoadingOverlay(
                isVisible = isLoading,
                message = "Creating clone..."
            )
        }
    }
}