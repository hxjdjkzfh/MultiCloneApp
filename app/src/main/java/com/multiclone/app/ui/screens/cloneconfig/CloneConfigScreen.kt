package com.multiclone.app.ui.screens.cloneconfig

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiclone.app.R
import com.multiclone.app.ui.components.LoadingIndicator

/**
 * Screen for configuring a clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    viewModel: CloneConfigViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val app by viewModel.appInfo.collectAsStateWithLifecycle()
    val cloneName by viewModel.cloneName.collectAsStateWithLifecycle()
    val customColor by viewModel.customColor.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val isEditMode by viewModel.isEditMode.collectAsStateWithLifecycle()
    
    // Advanced settings
    val useCustomIsolation by viewModel.useCustomIsolation.collectAsStateWithLifecycle()
    val isolateStorage by viewModel.isolateStorage.collectAsStateWithLifecycle()
    val isolateAccounts by viewModel.isolateAccounts.collectAsStateWithLifecycle()
    val isolateLocation by viewModel.isolateLocation.collectAsStateWithLifecycle()
    val useCustomLauncher by viewModel.useCustomLauncher.collectAsStateWithLifecycle()
    
    // UI state
    var showColorPicker by remember { mutableStateOf(false) }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    
    // Navigation when saved
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetSaveSuccess()
            onNavigateBack()
        }
    }
    
    // Error handling
    LaunchedEffect(error) {
        if (error != null) {
            // In a real app, would show a snackbar
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) 
                            stringResource(R.string.clone_config_edit_title) 
                        else 
                            stringResource(R.string.clone_config_new_title)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveClone() },
                        enabled = !isLoading && app != null && cloneName.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else if (app == null) {
                // Error state
                Text(
                    text = stringResource(R.string.clone_config_app_not_found),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // Config form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // App info section
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.clone_config_app_info_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Would show app icon here
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column {
                                    Text(
                                        text = app?.appName ?: "",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    
                                    Text(
                                        text = app?.packageName ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Basic settings
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.clone_config_basic_settings_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            // Clone name
                            OutlinedTextField(
                                value = cloneName,
                                onValueChange = { viewModel.setCloneName(it) },
                                label = { Text(stringResource(R.string.clone_config_name_label)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            // Color picker
                            Text(
                                text = stringResource(R.string.clone_config_color_label),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Color preview
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(customColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary)
                                        .border(
                                            BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                                            CircleShape
                                        )
                                        .clickable { showColorPicker = true }
                                )
                                
                                // Would have more color options in a full implementation
                                val predefinedColors = listOf(
                                    Color(0xFF1976D2), // Blue
                                    Color(0xFFE53935), // Red
                                    Color(0xFF43A047), // Green
                                    Color(0xFFFFB300), // Amber
                                    Color(0xFF8E24AA), // Purple
                                    Color(0xFF5D4037)  // Brown
                                )
                                
                                predefinedColors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .clickable { 
                                                viewModel.setCustomColor(color.value.toInt())
                                                showColorPicker = false
                                            }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Advanced settings toggle
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAdvancedSettings = !showAdvancedSettings }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.clone_config_advanced_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Switch(
                                checked = showAdvancedSettings,
                                onCheckedChange = { showAdvancedSettings = it }
                            )
                        }
                    }
                    
                    // Advanced settings
                    if (showAdvancedSettings) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Custom isolation toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.clone_config_custom_isolation),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = stringResource(R.string.clone_config_custom_isolation_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    
                                    Switch(
                                        checked = useCustomIsolation,
                                        onCheckedChange = { viewModel.toggleCustomIsolation(it) }
                                    )
                                }
                                
                                if (useCustomIsolation) {
                                    // Individual isolation settings
                                    
                                    // Storage isolation
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.clone_config_isolate_storage),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Switch(
                                            checked = isolateStorage,
                                            onCheckedChange = { viewModel.toggleIsolateStorage(it) }
                                        )
                                    }
                                    
                                    // Account isolation
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.clone_config_isolate_accounts),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Switch(
                                            checked = isolateAccounts,
                                            onCheckedChange = { viewModel.toggleIsolateAccounts(it) }
                                        )
                                    }
                                    
                                    // Location isolation
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.clone_config_isolate_location),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Switch(
                                            checked = isolateLocation,
                                            onCheckedChange = { viewModel.toggleIsolateLocation(it) }
                                        )
                                    }
                                }
                                
                                Divider()
                                
                                // Custom launcher toggle
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = stringResource(R.string.clone_config_custom_launcher),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = stringResource(R.string.clone_config_custom_launcher_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    
                                    Switch(
                                        checked = useCustomLauncher,
                                        onCheckedChange = { viewModel.toggleCustomLauncher(it) }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Save button
                    Button(
                        onClick = { viewModel.saveClone() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && app != null && cloneName.isNotBlank()
                    ) {
                        Text(stringResource(R.string.clone_config_save))
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}