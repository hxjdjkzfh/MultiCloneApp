package com.multiclone.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multiclone.app.R

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    // Settings state
    var enableNotifications by remember { mutableStateOf(true) }
    var autoStartEnabled by remember { mutableStateOf(false) }
    var autoCleanupEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var compactMode by remember { mutableStateOf(false) }
    
    // Advanced settings
    var advancedSettingsExpanded by remember { mutableStateOf(false) }
    var developerModeEnabled by remember { mutableStateOf(false) }
    var debugLoggingEnabled by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // General Settings
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_general_section),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_notifications),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.settings_notifications_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Switch(
                            checked = enableNotifications,
                            onCheckedChange = { enableNotifications = it }
                        )
                    }
                    
                    Divider()
                    
                    // Auto-start
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_auto_start),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.settings_auto_start_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Switch(
                            checked = autoStartEnabled,
                            onCheckedChange = { autoStartEnabled = it }
                        )
                    }
                    
                    Divider()
                    
                    // Auto-cleanup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_auto_cleanup),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.settings_auto_cleanup_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Switch(
                            checked = autoCleanupEnabled,
                            onCheckedChange = { autoCleanupEnabled = it }
                        )
                    }
                }
            }
            
            // Appearance Settings
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_appearance_section),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Dark mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_dark_mode),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.settings_dark_mode_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }
                    
                    Divider()
                    
                    // Compact mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.settings_compact_mode),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.settings_compact_mode_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        Switch(
                            checked = compactMode,
                            onCheckedChange = { compactMode = it }
                        )
                    }
                }
            }
            
            // Advanced Settings
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_advanced_section),
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        IconButton(onClick = { advancedSettingsExpanded = !advancedSettingsExpanded }) {
                            Icon(
                                imageVector = if (advancedSettingsExpanded) 
                                    Icons.Default.ArrowBack else 
                                    Icons.Default.ArrowBack, // Would be a dropdown icon
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    if (advancedSettingsExpanded) {
                        // Developer mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_developer_mode),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(R.string.settings_developer_mode_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            Switch(
                                checked = developerModeEnabled,
                                onCheckedChange = { developerModeEnabled = it }
                            )
                        }
                        
                        Divider()
                        
                        // Debug logging
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_debug_logging),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(R.string.settings_debug_logging_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            
                            Switch(
                                checked = debugLoggingEnabled,
                                onCheckedChange = { debugLoggingEnabled = it }
                            )
                        }
                    }
                }
            }
            
            // Version info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = stringResource(R.string.settings_version, "1.0.0"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}