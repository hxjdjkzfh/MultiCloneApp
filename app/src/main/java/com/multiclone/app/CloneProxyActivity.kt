package com.multiclone.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.multiclone.app.domain.usecase.LaunchCloneUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CloneProxyActivity : ComponentActivity() {
    
    @Inject
    lateinit var launchCloneUseCase: LaunchCloneUseCase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Extract clone ID from intent
        val cloneId = intent.getStringExtra("CLONE_ID")
        
        if (cloneId != null) {
            launchClone(cloneId)
        } else {
            Toast.makeText(this, "Failed to launch app: Missing clone ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun launchClone(cloneId: String) {
        lifecycleScope.launch {
            try {
                launchCloneUseCase(cloneId)
                // Activity should be finished after launching the cloned app
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CloneProxyActivity, "Error launching app: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}