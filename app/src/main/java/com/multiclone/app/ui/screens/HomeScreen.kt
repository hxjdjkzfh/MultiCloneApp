package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.utils.PermissionUtils
import com.multiclone.app.utils.PermissionHandler
import com.multiclone.app.viewmodel.ClonesListViewModel

/**
 * Main home screen of the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToClonesList: () -> Unit,
    onNavigateToAppSelection: () -> Unit,
    viewModel: ClonesListViewModel = hiltViewModel()
) {
    val clonesList by viewModel.recentClones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Load recent clones when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadRecentClones(5) // Load the 5 most recent clones
    }
    
    // Handle permissions
    val requiredPermissions = PermissionUtils.getRequiredPermissions()
    if (showPermissionDialog) {
        PermissionHandler(
            permissions = requiredPermissions,
            onPermissionsResult = { granted ->
                showPermissionDialog = false
                if (granted) {
                    // Permissions granted, reload data
                    viewModel.loadRecentClones(5)
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiClone") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigateToAppSelection()
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                text = { Text("Create Clone") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Recent clones section
            if (clonesList.isNotEmpty()) {
                Text(
                    text = "Recent Clones",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(clonesList) { clone ->
                        CloneItem(
                            cloneInfo = clone,
                            onClick = {
                                viewModel.launchClone(clone.id)
                            }
                        )
                    }
                }
                
                // Button to view all clones
                Button(
                    onClick = onNavigateToClonesList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.List, contentDescription = "List")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View All Clones")
                }
            } else {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Welcome to MultiClone",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Create your first app clone by tapping the button below.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = onNavigateToAppSelection
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create First Clone")
                            }
                        }
                    }
                }
            }
        }
        
        // Show loading overlay when loading
        LoadingOverlay(
            isVisible = isLoading,
            message = "Loading clones..."
        )
    }
}