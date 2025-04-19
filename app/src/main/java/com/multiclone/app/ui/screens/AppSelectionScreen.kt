package com.multiclone.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.ui.viewmodels.AppSelectionViewModel

/**
 * Screen for selecting an app to clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onNavigateBack: () -> Unit,
    onAppSelected: (AppInfo) -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val installedApps by viewModel.installedApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    
    // Filter apps based on search query
    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter { 
                it.appName.contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    // Handle error
    LaunchedEffect(error) {
        error?.let {
            // In a real app, you would show a snackbar or dialog
            // For now, just reset the error after showing it
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select App to Clone") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Refresh button could be added here
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Search Apps") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true
            )
            
            if (isLoading) {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredApps.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) 
                            "No apps found" 
                        else 
                            "No apps match '$searchQuery'",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // List of apps
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredApps) { app ->
                        AppItem(
                            app = app,
                            onClick = { onAppSelected(app) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual app item in the list
 */
@Composable
fun AppItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon
        Icon(
            imageVector = Icons.Default.Search, // Placeholder, should be app.icon
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp)
        )
        
        // App info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // App name
            Text(
                text = app.appName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // Package name
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}