package com.multiclone.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.data.model.AppInfo
import com.multiclone.app.ui.viewmodels.AppSelectionViewModel

/**
 * Screen for selecting an app to clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onAppSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select App to Clone") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            // App list
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (filteredApps.isEmpty()) {
                    EmptyAppsList(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    AppsList(
                        apps = filteredApps,
                        onAppSelected = onAppSelected
                    )
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
}

/**
 * Search bar for filtering apps
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search apps...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        modifier = modifier
    )
}

/**
 * List of installed apps
 */
@Composable
fun AppsList(
    apps: List<AppInfo>,
    onAppSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(apps) { app ->
            AppItem(
                app = app,
                onClick = { onAppSelected(app.packageName) }
            )
        }
    }
}

/**
 * Individual app item in the list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    bitmap = app.appIcon.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                )
            }
            
            // App info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Version: ${app.versionName}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Empty state when no apps match the search query
 */
@Composable
fun EmptyAppsList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Apps Found",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Try a different search query",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}