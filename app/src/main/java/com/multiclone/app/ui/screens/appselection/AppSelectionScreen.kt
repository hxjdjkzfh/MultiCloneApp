package com.multiclone.app.ui.screens.appselection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiclone.app.R
import com.multiclone.app.ui.components.AppListItem
import com.multiclone.app.ui.components.LoadingIndicator

/**
 * Screen for selecting an app to clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    viewModel: AppSelectionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCloneConfig: (String) -> Unit
) {
    val apps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Handle errors
    LaunchedEffect(error) {
        if (error != null) {
            // In real app, would show a snackbar
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_selection_title))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.app_selection_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
            
            if (isLoading) {
                LoadingIndicator()
            } else if (apps.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_selection_empty_state),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                // App list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(apps) { app ->
                        AppListItem(
                            appInfo = app,
                            onAppSelected = { onNavigateToCloneConfig(app.packageName) }
                        )
                    }
                }
            }
        }
    }
    
    // Filter dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text(stringResource(R.string.app_selection_filter_title)) },
            text = {
                Column {
                    RadioButton(
                        selected = filterState == 0,
                        onClick = { viewModel.setFilterState(0) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(stringResource(R.string.app_selection_filter_all), 
                        style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    RadioButton(
                        selected = filterState == 2,
                        onClick = { viewModel.setFilterState(2) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(stringResource(R.string.app_selection_filter_user), 
                        style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    RadioButton(
                        selected = filterState == 1,
                        onClick = { viewModel.setFilterState(1) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(stringResource(R.string.app_selection_filter_system), 
                        style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text(stringResource(R.string.app_selection_filter_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resetFilters()
                    showFilterDialog = false
                }) {
                    Text(stringResource(R.string.app_selection_filter_reset))
                }
            }
        )
    }
}