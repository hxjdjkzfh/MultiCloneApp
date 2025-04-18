package com.multiclone.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.virtualization.CloneManagerService;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

/**
 * Proxy activity that handles the launching of cloned apps
 * This is used as an intermediary to set up the virtualization environment
 * before launching the actual app UI
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u0012\u0010\u0013\u001a\u00020\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014J\b\u0010\u0016\u001a\u00020\u0012H\u0014J\u0010\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0004H\u0002J\u0010\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0004H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/multiclone/app/CloneProxyActivity;", "Landroidx/activity/ComponentActivity;", "()V", "TAG", "", "bound", "", "cloneManagerService", "Lcom/multiclone/app/domain/virtualization/CloneManagerService;", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "getCloneRepository", "()Lcom/multiclone/app/data/repository/CloneRepository;", "setCloneRepository", "(Lcom/multiclone/app/data/repository/CloneRepository;)V", "serviceConnection", "Landroid/content/ServiceConnection;", "handleAppLaunch", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "showError", "message", "showMessage", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class CloneProxyActivity extends androidx.activity.ComponentActivity {
    private final java.lang.String TAG = "CloneProxyActivity";
    @javax.inject.Inject()
    public com.multiclone.app.data.repository.CloneRepository cloneRepository;
    private com.multiclone.app.domain.virtualization.CloneManagerService cloneManagerService;
    private boolean bound = false;
    private final android.content.ServiceConnection serviceConnection = null;
    
    public CloneProxyActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.data.repository.CloneRepository getCloneRepository() {
        return null;
    }
    
    public final void setCloneRepository(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    /**
     * Handle the app launch once we're connected to the service
     */
    private final void handleAppLaunch() {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    private final void showMessage(java.lang.String message) {
    }
}