package com.multiclone.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.CloneItem
import com.multiclone.app.viewmodel.ClonesListViewModel
import kotlinx.coroutines.launch

/**
 * Screen displaying a list of cloned apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClonesListScreen(
    onNavigateToAppSelection: () -> Unit,
    viewModel: ClonesListViewModel = hiltViewModel()
) {
    val clones by viewModel.clones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Show error message in snackbar if any
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cloned Apps") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Clone"
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (clones.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "No cloned apps yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tap the + button to create your first clone",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                ) {
                    items(clones) { clone ->
                        CloneItem(
                            cloneInfo = clone,
                            onClick = { viewModel.launchClone(clone.id) },
                            onLongClick = { /* Show delete dialog */ }
                        )
                    }
                }
            }
        }
    }
}