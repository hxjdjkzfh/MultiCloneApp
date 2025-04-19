package com.multiclone.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.components.CloneListItem
import com.multiclone.app.ui.components.LoadingIndicator

/**
 * Main home screen showing all cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddCloneClick: () -> Unit,
    onCloneLaunch: (String) -> Unit,
    onCloneInfo: (String) -> Unit,
    onCloneSettings: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error messages in a snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    // State for delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cloneToDelete by remember { mutableStateOf<CloneInfo?>(null) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MultiClone App") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadClones() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCloneClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Clone")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingIndicator(message = "Loading your cloned apps...")
            } else if (uiState.clones.isEmpty()) {
                EmptyClonesList(onAddCloneClick)
            } else {
                ClonesList(
                    clones = uiState.clones,
                    onLaunchClick = { clone ->
                        viewModel.launchClone(clone.id)
                        onCloneLaunch(clone.id)
                    },
                    onInfoClick = { clone -> onCloneInfo(clone.id) },
                    onSettingsClick = { clone -> onCloneSettings(clone.id) },
                    onDeleteClick = { clone ->
                        cloneToDelete = clone
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && cloneToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Clone") },
            text = { Text("Are you sure you want to delete ${cloneToDelete?.cloneName}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        cloneToDelete?.let { viewModel.deleteClone(it.id) }
                        showDeleteDialog = false
                        cloneToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Shows a list of cloned apps
 */
@Composable
private fun ClonesList(
    clones: List<CloneInfo>,
    onLaunchClick: (CloneInfo) -> Unit,
    onInfoClick: (CloneInfo) -> Unit,
    onSettingsClick: (CloneInfo) -> Unit,
    onDeleteClick: (CloneInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Your Cloned Apps",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(clones) { clone ->
            CloneListItem(
                cloneInfo = clone,
                onLaunchClick = onLaunchClick,
                onInfoClick = onInfoClick,
                onSettingsClick = onSettingsClick,
                onDeleteClick = onDeleteClick
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Add some space at the bottom for the FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Shows when no clones are available
 */
@Composable
private fun EmptyClonesList(onAddCloneClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))
        
        Text(
            text = "No Cloned Apps Yet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Create your first app clone by tapping the + button below.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        EmptyStateButton(
            text = "Create First Clone",
            icon = Icons.Default.Add,
            onClick = onAddCloneClick
        )
        
        Spacer(modifier = Modifier.weight(0.7f))
    }
}

/**
 * Button shown in the empty state
 */
@Composable
private fun EmptyStateButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}