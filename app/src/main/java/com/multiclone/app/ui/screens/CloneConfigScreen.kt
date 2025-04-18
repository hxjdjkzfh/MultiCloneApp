package com.multiclone.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.multiclone.app.ui.components.LoadingOverlay
import com.multiclone.app.viewmodel.CloneConfigViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloneConfigScreen(
    navController: NavController,
    packageName: String,
    viewModel: CloneConfigViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appInfo by viewModel.appInfo.collectAsState()
    val cloneName by viewModel.cloneName.collectAsState()
    val customIcon by viewModel.customIcon.collectAsState()
    val isCreatingClone by viewModel.isCreatingClone.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isCloneCreated by viewModel.isCloneCreated.collectAsState()
    
    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onCustomIconSelected(it, context) }
    }
    
    LaunchedEffect(packageName) {
        viewModel.loadAppInfo(packageName)
    }
    
    LaunchedEffect(isCloneCreated) {
        if (isCloneCreated) {
            navController.navigate("clones_list") {
                popUpTo("app_selection") { inclusive = true }
            }
        }
    }
    
    var showErrorDialog by remember { mutableStateOf(false) }
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            showErrorDialog = true
        }
    }
    
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { 
                showErrorDialog = false
                viewModel.clearErrorMessage()
            },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showErrorDialog = false
                        viewModel.clearErrorMessage()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configure Clone") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App info card
                appInfo?.let { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // App icon
                            app.icon?.let { icon ->
                                Image(
                                    bitmap = icon.asImageBitmap(),
                                    contentDescription = "App Icon",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // App name
                            Text(
                                text = app.name,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Package name
                            Text(
                                text = app.packageName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Clone name
                AnimatedVisibility(
                    visible = appInfo != null,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Clone Name",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = cloneName,
                            onValueChange = { viewModel.onCloneNameChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Enter clone name") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Custom icon selector
                AnimatedVisibility(
                    visible = appInfo != null,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Custom Icon",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Icon preview
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { iconPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            customIcon?.let { icon ->
                                Image(
                                    bitmap = icon.asImageBitmap(),
                                    contentDescription = "Custom Icon",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: appInfo?.icon?.let { defaultIcon ->
                                Image(
                                    bitmap = defaultIcon.asImageBitmap(),
                                    contentDescription = "App Icon",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Pick icon",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextButton(onClick = { iconPickerLauncher.launch("image/*") }) {
                            Text("Change Icon")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Create clone button
                Button(
                    onClick = { viewModel.createClone(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = appInfo != null && cloneName.isNotEmpty() && !isCreatingClone
                ) {
                    Text("Create Clone")
                }
            }
            
            // Loading overlay
            LoadingOverlay(isLoading = isCreatingClone)
        }
    }
}
