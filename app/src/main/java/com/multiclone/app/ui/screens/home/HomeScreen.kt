package com.multiclone.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.ui.components.EmptyState
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.ui.theme.MultiCloneTheme

/**
 * The main home screen showing the user's cloned apps
 * and providing actions to manage them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAppSelection: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToEditClone: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("MultiClone")
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "About"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Clone a new app"
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Content based on state
                when {
                    uiState.isLoading -> {
                        // Show loading state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.clones.isEmpty() -> {
                        // Show empty state
                        EmptyState(
                            message = "You haven't cloned any apps yet. Tap the + button to get started!",
                            icon = Icons.Default.Apps,
                            buttonText = "Clone an App",
                            onButtonClick = onNavigateToAppSelection,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        // Show list of clones
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(uiState.clones) { clone ->
                                CloneItem(
                                    clone = clone,
                                    onLaunchClick = { viewModel.launchClone(clone.id) },
                                    onEditClick = { onNavigateToEditClone(clone.id) },
                                    onDeleteClick = { viewModel.deleteClone(clone.id) }
                                )
                            }
                        }
                    }
                }
                
                // Show loading overlay during operations
                LoadingOverlay(
                    isLoading = uiState.isOperationInProgress,
                    message = uiState.operationMessage
                )
                
                // Error message
                if (uiState.errorMessage != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissError() },
                        title = { Text("Error") },
                        text = { Text(uiState.errorMessage!!) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.dismissError() }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MultiCloneTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val previewViewModel = HomeViewModel.createPreview()
            
            HomeScreen(
                onNavigateToAppSelection = {},
                onNavigateToSettings = {},
                onNavigateToAbout = {},
                onNavigateToEditClone = {},
                viewModel = previewViewModel
            )
        }
    }
}