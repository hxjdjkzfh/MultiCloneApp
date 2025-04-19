package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.viewmodels.HomeViewModel

/**
 * Home screen showing all cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAppSelection: () -> Unit,
    onNavigateToCloneDetails: (String) -> Unit,
    onLaunchClone: (String, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val clones by viewModel.clones.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiClone App") },
                actions = {
                    // Settings button can be added here
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new clone"
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
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (clones.isEmpty()) {
                // Show empty state
                EmptyHomeScreen(onNavigateToAppSelection)
            } else {
                // Show clones grid
                CloneGrid(
                    clones = clones,
                    onCloneClick = { clone ->
                        onLaunchClone(clone.packageName, clone.id)
                    },
                    onCloneLongClick = { clone ->
                        onNavigateToCloneDetails(clone.id)
                    }
                )
            }
        }
    }
}

/**
 * Grid display of cloned apps
 */
@Composable
fun CloneGrid(
    clones: List<CloneInfo>,
    onCloneClick: (CloneInfo) -> Unit,
    onCloneLongClick: (CloneInfo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(clones) { clone ->
            CloneItem(
                clone = clone,
                onClick = { onCloneClick(clone) },
                onLongClick = { onCloneLongClick(clone) }
            )
        }
    }
}

/**
 * Individual clone app item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneItem(
    clone: CloneInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // App Icon
            Icon(
                imageVector = Icons.Default.Add, // Placeholder, should be replaced with app icon
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
            
            // App Name
            Text(
                text = clone.displayName,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Empty state when no clones exist
 */
@Composable
fun EmptyHomeScreen(onNavigateToAppSelection: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No cloned apps yet",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Tap the + button to clone your first app",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToAppSelection
        ) {
            Text("Clone New App")
        }
    }
}