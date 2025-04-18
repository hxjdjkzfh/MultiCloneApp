package com.multiclone.app.domain.virtualization;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.data.repository.CloneRepository;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.Dispatchers;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

/**
 * Background service for managing running cloned apps
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0002-.B\u0005\u00a2\u0006\u0002\u0010\u0002J\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00040\u0019J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u000e\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u0004J\u0010\u0010!\u001a\u00020\"2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\b\u0010#\u001a\u00020\u001bH\u0016J\b\u0010$\u001a\u00020\u001bH\u0016J\"\u0010%\u001a\u00020&2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\'\u001a\u00020&2\u0006\u0010(\u001a\u00020&H\u0016J\u000e\u0010)\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020+J\u000e\u0010,\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u00060\u0006R\u00020\u0000X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\b8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001e\u0010\r\u001a\u0012\u0012\u0004\u0012\u00020\u0004\u0012\b\u0012\u00060\u000fR\u00020\u00000\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0012\u001a\u00020\u00138\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017\u00a8\u0006/"}, d2 = {"Lcom/multiclone/app/domain/virtualization/CloneManagerService;", "Landroid/app/Service;", "()V", "TAG", "", "binder", "Lcom/multiclone/app/domain/virtualization/CloneManagerService$LocalBinder;", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "getCloneRepository", "()Lcom/multiclone/app/data/repository/CloneRepository;", "setCloneRepository", "(Lcom/multiclone/app/data/repository/CloneRepository;)V", "runningClones", "Ljava/util/concurrent/ConcurrentHashMap;", "Lcom/multiclone/app/domain/virtualization/CloneManagerService$CloneSession;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "virtualAppEngine", "Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "getVirtualAppEngine", "()Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "setVirtualAppEngine", "(Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;)V", "getRunningClones", "", "handleIntent", "", "intent", "Landroid/content/Intent;", "isCloneRunning", "", "cloneId", "onBind", "Landroid/os/IBinder;", "onCreate", "onDestroy", "onStartCommand", "", "flags", "startId", "startCloneSession", "cloneInfo", "Lcom/multiclone/app/data/model/CloneInfo;", "stopCloneSession", "CloneSession", "LocalBinder", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class CloneManagerService extends android.app.Service {
    private final java.lang.String TAG = "CloneManagerService";
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, com.multiclone.app.domain.virtualization.CloneManagerService.CloneSession> runningClones = null;
    @javax.inject.Inject()
    public com.multiclone.app.data.repository.CloneRepository cloneRepository;
    @javax.inject.Inject()
    public com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine;
    private final com.multiclone.app.domain.virtualization.CloneManagerService.LocalBinder binder = null;
    
    public CloneManagerService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.data.repository.CloneRepository getCloneRepository() {
        return null;
    }
    
    public final void setCloneRepository(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.domain.virtualization.VirtualAppEngine getVirtualAppEngine() {
        return null;
    }
    
    public final void setVirtualAppEngine(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.os.IBinder onBind(@org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    /**
     * Start a cloned app session
     */
    public final boolean startCloneSession(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.CloneInfo cloneInfo) {
        return false;
    }
    
    /**
     * Stop a cloned app session
     */
    public final boolean stopCloneSession(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
        return false;
    }
    
    /**
     * Check if a cloned app is currently running
     */
    public final boolean isCloneRunning(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
        return false;
    }
    
    /**
     * Get a list of all running clones
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getRunningClones() {
        return null;
    }
    
    /**
     * Handle intents sent to the service
     */
    private final void handleIntent(android.content.Intent intent) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/multiclone/app/domain/virtualization/CloneManagerService$LocalBinder;", "Landroid/os/Binder;", "(Lcom/multiclone/app/domain/virtualization/CloneManagerService;)V", "getService", "Lcom/multiclone/app/domain/virtualization/CloneManagerService;", "app_debug"})
    public final class LocalBinder extends android.os.Binder {
        
        public LocalBinder() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.multiclone.app.domain.virtualization.CloneManagerService getService() {
            return null;
        }
    }
    
    /**
     * Inner class representing a running clone session
     */
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\t\u001a\u00020\bJ\u0006\u0010\n\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/multiclone/app/domain/virtualization/CloneManagerService$CloneSession;", "", "cloneInfo", "Lcom/multiclone/app/data/model/CloneInfo;", "service", "Lcom/multiclone/app/domain/virtualization/CloneManagerService;", "(Lcom/multiclone/app/domain/virtualization/CloneManagerService;Lcom/multiclone/app/data/model/CloneInfo;Lcom/multiclone/app/domain/virtualization/CloneManagerService;)V", "isRunning", "", "start", "stop", "app_debug"})
    public final class CloneSession {
        private final com.multiclone.app.data.model.CloneInfo cloneInfo = null;
        private final com.multiclone.app.domain.virtualization.CloneManagerService service = null;
        private boolean isRunning = false;
        
        public CloneSession(@org.jetbrains.annotations.NotNull()
        com.multiclone.app.data.model.CloneInfo cloneInfo, @org.jetbrains.annotations.NotNull()
        com.multiclone.app.domain.virtualization.CloneManagerService service) {
            super();
        }
        
        /**
         * Start the clone session
         */
        public final boolean start() {
            return false;
        }
        
        /**
         * Stop the clone session
         */
        public final boolean stop() {
            return false;
        }
    }
}