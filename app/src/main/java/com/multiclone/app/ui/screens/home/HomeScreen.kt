package com.multiclone.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.R
import com.multiclone.app.ui.screens.home.components.CloneList
import com.multiclone.app.ui.screens.home.components.EmptyState

/**
 * Home screen component displaying the list of cloned apps.
 * 
 * @param onNavigateToAppSelection Callback to navigate to app selection screen
 * @param onNavigateToSettings Callback to navigate to settings screen
 * @param onNavigateToAbout Callback to navigate to about screen
 * @param onNavigateToEditClone Callback to navigate to edit clone screen
 * @param viewModel ViewModel for this screen (injected by Hilt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAppSelection: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToEditClone: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Collect state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.home_title))
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = stringResource(id = R.string.nav_settings)
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = stringResource(id = R.string.nav_about)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Button(onClick = onNavigateToAppSelection) {
                Text(text = "+")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.clones.isEmpty()) {
                // Show empty state
                EmptyState(
                    onCreateClone = onNavigateToAppSelection
                )
            } else {
                // Show list of clones
                CloneList(
                    clones = uiState.clones,
                    onCloneClick = viewModel::launchClone,
                    onCloneEdit = onNavigateToEditClone,
                    onCloneDelete = viewModel::deleteClone
                )
            }
        }
    }
}