package com.multiclone.app.ui.screens

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.utils.IconUtils
import com.multiclone.app.viewmodel.CloneConfigViewModel

/**
 * Screen for configuring a new clone
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    packageName: String,
    onNavigateUp: () -> Unit,
    onCloneCreated: () -> Unit,
    viewModel: CloneConfigViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appInfo by viewModel.appInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var customName by remember { mutableStateOf("") }
    var useCustomIcon by remember { mutableStateOf(false) }
    var customIcon by remember(appInfo) { 
        mutableStateOf<Bitmap?>(null) 
    }
    
    // Prepare the custom icon to be the same as the original by default
    LaunchedEffect(appInfo) {
        appInfo?.let {
            if (customIcon == null) {
                customIcon = IconUtils.drawableToBitmap(it.icon)
            }
        }
    }
    
    // Load app info when the screen is first displayed
    LaunchedEffect(key1 = packageName) {
        viewModel.loadAppInfo(packageName)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configure Clone") },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App info section
            appInfo?.let { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App icon
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (useCustomIcon && customIcon != null) {
                                Image(
                                    bitmap = customIcon!!.asImageBitmap(),
                                    contentDescription = "App icon",
                                    modifier = Modifier.size(80.dp)
                                )
                                
                                // Edit icon button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            // In a real app, this would open an icon picker
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit icon",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Image(
                                    bitmap = IconUtils.drawableToBitmap(app.icon).asImageBitmap(),
                                    contentDescription = "App icon",
                                    modifier = Modifier.size(80.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // App name
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Package name
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (!app.versionName.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "v${app.versionName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Configuration section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Clone Configuration",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Custom name field
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Custom Name (Optional)") },
                            placeholder = { Text(app.appName) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Custom icon toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Use Custom Icon",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Switch(
                                checked = useCustomIcon,
                                onCheckedChange = { useCustomIcon = it }
                            )
                        }
                        
                        if (useCustomIcon) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Customize the icon in a real implementation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Create button
                Button(
                    onClick = {
                        viewModel.createClone(
                            packageName = app.packageName,
                            customName = if (customName.isBlank()) null else customName,
                            customIcon = if (useCustomIcon) customIcon else null,
                            onSuccess = {
                                onCloneCreated()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Create")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Clone")
                }
                
                // Error message
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        // Show loading overlay when loading
        LoadingOverlay(
            isVisible = isLoading,
            message = if (appInfo == null) "Loading app info..." else "Creating clone..."
        )
    }
}