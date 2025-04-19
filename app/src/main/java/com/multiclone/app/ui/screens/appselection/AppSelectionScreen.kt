package com.multiclone.app.ui.screens.appselection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.AppItem
import com.multiclone.app.ui.components.EmptyState
import com.multiclone.app.ui.components.LoadingDialog
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.ui.theme.MultiCloneTheme

/**
 * Screen for selecting an app to clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCloneSetup: (String) -> Unit,
    viewModel: AppSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
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
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            viewModel.filterApps(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search apps...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
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
                        uiState.filteredApps.isEmpty() -> {
                            // Show empty state
                            if (searchQuery.isBlank()) {
                                EmptyState(
                                    message = "No apps found on your device that can be cloned.",
                                    icon = Icons.Outlined.Search,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                EmptyState(
                                    message = "No apps found matching \"$searchQuery\".",
                                    icon = Icons.Outlined.Search,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        else -> {
                            // Show list of apps
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(uiState.filteredApps) { appInfo ->
                                    AppItem(
                                        appInfo = appInfo,
                                        onClick = { 
                                            viewModel.selectApp(appInfo.packageName)
                                            onNavigateToCloneSetup(appInfo.packageName)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
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
    
    // Loading dialog when creating a clone
    LoadingDialog(
        isLoading = uiState.isCreatingClone,
        message = "Preparing to create clone..."
    )
}

@Preview(showBackground = true)
@Composable
fun AppSelectionScreenPreview() {
    MultiCloneTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val previewViewModel = AppSelectionViewModel.createPreview()
            
            AppSelectionScreen(
                onNavigateBack = {},
                onNavigateToCloneSetup = {},
                viewModel = previewViewModel
            )
        }
    }
}