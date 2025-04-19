package com.multiclone.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
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
import com.multiclone.app.data.model.CloneInfo
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.ui.components.EmptyState
import com.multiclone.app.ui.components.LoadingIndicator

/**
 * Home screen displaying the list of cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAppSelection: () -> Unit,
    onNavigateToCloneConfig: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val clones by viewModel.clones.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }
    
    // Error handling
    LaunchedEffect(error) {
        if (error != null) {
            // In a real implementation, we would show a snackbar or dialog
            // For now, just clear the error
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.home_title))
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.nav_settings)) },
                            leadingIcon = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.nav_about)) },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null)
                            },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.home_create_first_clone)
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
                LoadingIndicator()
            } else if (clones.isEmpty()) {
                EmptyState(
                    message = stringResource(R.string.home_empty_state),
                    actionText = stringResource(R.string.home_create_first_clone),
                    onAction = onNavigateToAppSelection
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clones) { clone ->
                        CloneItem(
                            cloneInfo = clone,
                            onCloneClick = { viewModel.launchClone(clone.id) },
                            onEditClick = { onNavigateToCloneConfig(clone.id) },
                            onDeleteClick = { viewModel.deleteClone(clone.id) }
                        )
                    }
                }
            }
        }
    }
}