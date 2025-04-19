package com.multiclone.app.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multiclone.app.R

/**
 * About screen with app information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.about_title))
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                // App icon would go here - using a Box as placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // In a real app, would use an actual image resource
                    // Image(painter = painterResource(id = R.drawable.app_icon), contentDescription = null)
                    Text(
                        text = "MC",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name and version
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = stringResource(R.string.about_version, "1.0.0"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App description
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.about_description_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Features
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.about_features_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // List of features
                    val features = listOf(
                        R.string.about_feature_1,
                        R.string.about_feature_2,
                        R.string.about_feature_3,
                        R.string.about_feature_4,
                        R.string.about_feature_5
                    )
                    
                    features.forEach { stringResId ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Bullet point
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                            )
                            
                            // Feature text
                            Text(
                                text = stringResource(stringResId),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer info
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.about_developer_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(R.string.about_developer_name),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = stringResource(R.string.about_developer_contact),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Copyright
            Text(
                text = stringResource(R.string.about_copyright, "2025"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}