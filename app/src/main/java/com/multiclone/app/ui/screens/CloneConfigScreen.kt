package com.multiclone.app.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.ui.theme.AccentBlue
import com.multiclone.app.ui.theme.AccentGreen
import com.multiclone.app.ui.theme.AccentOrange
import com.multiclone.app.ui.theme.AccentPurple
import com.multiclone.app.ui.theme.AccentRed
import com.multiclone.app.ui.theme.MultiCloneTheme
import com.multiclone.app.viewmodel.CloneConfigViewModel

/**
 * Screen for configuring a new app clone (name, icon, etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    packageName: String,
    onBackPressed: () -> Unit,
    onCloneCreated: () -> Unit,
    viewModel: CloneConfigViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var cloneName by remember { mutableStateOf("") }
    var createShortcut by remember { mutableStateOf(true) }
    var selectedIconColor by remember { mutableStateOf(AccentBlue) }
    
    // Initialize UI state with selected package
    remember(packageName) {
        viewModel.initialize(packageName)
        true
    }
    
    // Set default clone name when original app name is loaded
    remember(uiState.originalAppName) {
        if (cloneName.isEmpty() && uiState.originalAppName.isNotEmpty()) {
            cloneName = "Clone of ${uiState.originalAppName}"
        }
        true
    }
    
    // Generate custom icon if needed
    val customIcon = remember(selectedIconColor, uiState.originalAppIcon) {
        uiState.originalAppIcon?.let { icon ->
            generateCustomIcon(icon, selectedIconColor.hashCode())
        }
    }
    
    // Handle successful clone creation
    if (uiState.cloneCreated) {
        onCloneCreated()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configure Clone",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                // Show loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                // Clone configuration content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App info header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Original app icon
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.originalAppIcon != null) {
                                Image(
                                    bitmap = uiState.originalAppIcon.asImageBitmap(),
                                    contentDescription = "Original app icon",
                                    modifier = Modifier.size(72.dp)
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // App info
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = uiState.originalAppName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = packageName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Clone name field
                    OutlinedTextField(
                        value = cloneName,
                        onValueChange = { cloneName = it },
                        label = { Text("Clone Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit name",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Custom icon color selection
                    Text(
                        text = "Choose Icon Color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ColorOption(
                            color = AccentBlue,
                            isSelected = selectedIconColor == AccentBlue,
                            onClick = { selectedIconColor = AccentBlue }
                        )
                        ColorOption(
                            color = AccentRed,
                            isSelected = selectedIconColor == AccentRed,
                            onClick = { selectedIconColor = AccentRed }
                        )
                        ColorOption(
                            color = AccentGreen,
                            isSelected = selectedIconColor == AccentGreen,
                            onClick = { selectedIconColor = AccentGreen }
                        )
                        ColorOption(
                            color = AccentOrange,
                            isSelected = selectedIconColor == AccentOrange,
                            onClick = { selectedIconColor = AccentOrange }
                        )
                        ColorOption(
                            color = AccentPurple,
                            isSelected = selectedIconColor == AccentPurple,
                            onClick = { selectedIconColor = AccentPurple }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Preview of custom icon
                    Text(
                        text = "Icon Preview",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (customIcon != null) {
                            Image(
                                bitmap = customIcon.asImageBitmap(),
                                contentDescription = "Custom icon preview",
                                modifier = Modifier.size(100.dp)
                            )
                        } else if (uiState.originalAppIcon != null) {
                            Image(
                                bitmap = uiState.originalAppIcon.asImageBitmap(),
                                contentDescription = "Original icon preview",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Create shortcut option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create Home Screen Shortcut",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Switch(
                            checked = createShortcut,
                            onCheckedChange = { createShortcut = it }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Create clone button
                    Button(
                        onClick = {
                            viewModel.createClone(
                                packageName = packageName,
                                cloneName = cloneName.ifEmpty { "Clone of ${uiState.originalAppName}" },
                                customIconColor = selectedIconColor,
                                createShortcut = createShortcut
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !uiState.isCreatingClone
                    ) {
                        Text("Create Clone")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Description
                    Text(
                        text = "This will create an isolated clone of the app with separate data and settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    // Show loading overlay when creating clone
    LoadingOverlay(
        isLoading = uiState.isCreatingClone,
        message = "Creating clone..."
    )
}

/**
 * Color selection option for custom icon
 */
@Composable
private fun ColorOption(
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
}

/**
 * Generate a custom icon based on the original icon and a color
 */
private fun generateCustomIcon(originalIcon: Bitmap, colorSeed: Int): Bitmap {
    val iconSize = originalIcon.width
    val result = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint()
    
    // Draw background circle with custom color
    paint.color = Color.HSVToColor(
        floatArrayOf(
            (colorSeed % 360).toFloat(),
            0.8f,
            0.9f
        )
    )
    
    // Draw circular background
    val padding = iconSize * 0.15f
    canvas.drawCircle(
        iconSize / 2f,
        iconSize / 2f,
        iconSize / 2f - padding,
        paint
    )
    
    // Draw the original icon on top (slightly smaller)
    val scaledIcon = Bitmap.createScaledBitmap(
        originalIcon,
        (iconSize * 0.7f).toInt(),
        (iconSize * 0.7f).toInt(),
        true
    )
    
    canvas.drawBitmap(
        scaledIcon,
        (iconSize - scaledIcon.width) / 2f,
        (iconSize - scaledIcon.height) / 2f,
        null
    )
    
    return result
}

/**
 * Preview for the clone config screen
 */
@Preview(showBackground = true)
@Composable
private fun CloneConfigScreenPreview() {
    MultiCloneTheme {
        // Preview content
    }
}