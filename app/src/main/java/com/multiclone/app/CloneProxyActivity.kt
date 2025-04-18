package com.multiclone.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiclone.app.domain.virtualization.VirtualAppEngine
import com.multiclone.app.ui.theme.MultiCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Proxy activity that handles launching of cloned applications.
 * This activity serves as a bridge between the launcher shortcut and the virtual app environment.
 */
@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    @Inject
    lateinit var virtualAppEngine: VirtualAppEngine
    
    companion object {
        private const val TAG = "CloneProxyActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MultiCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // Get clone information from intent
        val clonePackageName = intent.getStringExtra("clone_package_name")
        val cloneId = intent.getStringExtra("clone_id")
        
        if (clonePackageName.isNullOrEmpty()) {
            Log.e(TAG, "No clone package name provided")
            Toast.makeText(this, "Error: No clone information provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Launch the clone
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = virtualAppEngine.launchClone(clonePackageName)
                if (!success) {
                    Toast.makeText(this@CloneProxyActivity, "Failed to launch clone", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error launching clone", e)
                Toast.makeText(this@CloneProxyActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Always finish this activity after attempting to launch
                finish()
            }
        }
    }
}
