package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.ui.theme.MultiCloneTheme
import com.multiclone.app.viewmodel.ClonesListViewModel

/**
 * Screen that displays a list of all cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClonesListScreen(
    onBackPressed: () -> Unit,
    navigateToCreateClone: () -> Unit,
    viewModel: ClonesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cloned Apps",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToCreateClone,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new clone"
                )
            }
        }
    ) { paddingValues ->
        ClonesListContent(
            paddingValues = paddingValues,
            isLoading = uiState.isLoading,
            clones = uiState.clones,
            onCloneClick = { cloneId ->
                viewModel.launchClone(cloneId)
            },
            onCreateShortcut = { cloneId ->
                viewModel.createShortcut(cloneId)
            }
        )
    }
    
    // Show loading overlay during operations
    LoadingOverlay(
        isLoading = uiState.isCreatingShortcut || uiState.isLaunchingClone,
        message = if (uiState.isCreatingShortcut) "Creating shortcut..." else "Launching clone..."
    )
}

/**
 * Content for the clones list screen
 */
@Composable
private fun ClonesListContent(
    paddingValues: PaddingValues,
    isLoading: Boolean,
    clones: List<CloneInfo>,
    onCloneClick: (String) -> Unit,
    onCreateShortcut: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (isLoading) {
            // Show loading indicator
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (clones.isEmpty()) {
            // Show empty state
            EmptyClonesList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        } else {
            // Show list of clones
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = clones,
                    key = { it.id }
                ) { cloneInfo ->
                    CloneItem(
                        cloneInfo = cloneInfo,
                        onClick = { onCloneClick(cloneInfo.id) },
                        onCreateShortcut = { onCreateShortcut(cloneInfo.id) }
                    )
                }
                
                // Add bottom space for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/**
 * Empty state when no clones are available
 */
@Composable
private fun EmptyClonesList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "No cloned apps yet",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the + button to create your first clone",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Preview for the clones list screen
 */
@Preview(showBackground = true)
@Composable
private fun ClonesListScreenPreview() {
    MultiCloneTheme {
        // Preview content
    }
}