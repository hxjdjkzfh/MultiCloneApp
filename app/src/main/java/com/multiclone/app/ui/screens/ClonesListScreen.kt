package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.viewmodel.ClonesListViewModel

/**
 * Screen for displaying all cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClonesListScreen(
    onNavigateUp: () -> Unit,
    onNavigateToAppSelection: () -> Unit,
    viewModel: ClonesListViewModel = hiltViewModel()
) {
    val clonesList by viewModel.allClones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedClone by remember { mutableStateOf<CloneInfo?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Load all clones when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadAllClones()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Clones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (clonesList.isEmpty() && !isLoading) {
                // No clones yet
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Clones Yet",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Create your first clone by tapping the + button",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(onClick = onNavigateToAppSelection) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Clone")
                        }
                    }
                }
            } else {
                // Show clone list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(clonesList) { clone ->
                        CloneItem(
                            cloneInfo = clone,
                            onClick = {
                                viewModel.launchClone(clone.id)
                            },
                            onLongClick = {
                                selectedClone = clone
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog && selectedClone != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedClone = null
                },
                title = { Text("Delete Clone") },
                text = { 
                    Text("Are you sure you want to delete ${selectedClone?.displayName}? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedClone?.let {
                                viewModel.deleteClone(it.id)
                            }
                            showDeleteDialog = false
                            selectedClone = null
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            selectedClone = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Show loading overlay when loading
        LoadingOverlay(
            isVisible = isLoading,
            message = "Loading clones..."
        )
    }
}