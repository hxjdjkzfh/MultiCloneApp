package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import com.multiclone.app.ui.components.AppItem
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.utils.PermissionHandler
import com.multiclone.app.utils.PermissionUtils
import com.multiclone.app.viewmodel.AppSelectionViewModel

/**
 * Screen for selecting an app to clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onNavigateUp: () -> Unit,
    onAppSelected: (String) -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val apps by viewModel.appsList.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(true) }
    
    // Handle permissions
    val requiredPermissions = PermissionUtils.getRequiredPermissions()
    if (showPermissionDialog) {
        PermissionHandler(
            permissions = requiredPermissions,
            onPermissionsResult = { granted ->
                showPermissionDialog = false
                if (granted) {
                    // Permissions granted, load the app list
                    viewModel.loadInstalledApps()
                }
            }
        )
    }
    
    // Load installed apps when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadInstalledApps()
    }
    
    // Handle search queries
    LaunchedEffect(searchQuery) {
        viewModel.filterApps(searchQuery)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select App to Clone") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { searchQuery = it },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Search apps") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search suggestions would go here if needed
            }
            
            // App list
            if (filteredApps.isEmpty() && !isLoading) {
                // No apps found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No apps found" + 
                            if (searchQuery.isNotEmpty()) " for '$searchQuery'" else "",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // App list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps) { app ->
                        AppItem(
                            appInfo = app,
                            onClick = {
                                onAppSelected(app.packageName)
                            }
                        )
                    }
                }
            }
        }
        
        // Show loading overlay when loading
        LoadingOverlay(
            isVisible = isLoading,
            message = "Loading installed apps..."
        )
    }
}