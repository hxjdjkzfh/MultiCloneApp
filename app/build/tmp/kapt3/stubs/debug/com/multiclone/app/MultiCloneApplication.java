package com.multiclone.app;

import android.app.Application;
import android.content.Intent;
import com.multiclone.app.core.virtualization.CloneManagerService;
import com.multiclone.app.domain.service.VirtualAppService;
import dagger.hilt.android.HiltAndroidApp;
import javax.inject.Inject;

/**
 * Main Application class that initializes app-wide dependencies
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\nH\u0016R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\f"}, d2 = {"Lcom/multiclone/app/MultiCloneApplication;", "Landroid/app/Application;", "()V", "virtualAppService", "Lcom/multiclone/app/domain/service/VirtualAppService;", "getVirtualAppService", "()Lcom/multiclone/app/domain/service/VirtualAppService;", "setVirtualAppService", "(Lcom/multiclone/app/domain/service/VirtualAppService;)V", "onCreate", "", "onTerminate", "app_debug"})
@dagger.hilt.android.HiltAndroidApp()
public final class MultiCloneApplication extends android.app.Application {
    @javax.inject.Inject()
    public com.multiclone.app.domain.service.VirtualAppService virtualAppService;
    
    public MultiCloneApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.domain.service.VirtualAppService getVirtualAppService() {
        return null;
    }
    
    public final void setVirtualAppService(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.service.VirtualAppService p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @java.lang.Override()
    public void onTerminate() {
    }
}