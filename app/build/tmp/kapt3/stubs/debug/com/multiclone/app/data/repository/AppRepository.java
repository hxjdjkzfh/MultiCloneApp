package com.multiclone.app.data.repository;

import com.multiclone.app.core.virtualization.VirtualAppEngine;
import com.multiclone.app.data.model.AppInfo;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository for accessing installed applications data
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bJ\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nJ\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\n2\u0006\u0010\f\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/multiclone/app/data/repository/AppRepository;", "", "virtualAppEngine", "Lcom/multiclone/app/core/virtualization/VirtualAppEngine;", "(Lcom/multiclone/app/core/virtualization/VirtualAppEngine;)V", "getAppInfo", "Lcom/multiclone/app/data/model/AppInfo;", "packageName", "", "getInstalledApps", "", "searchInstalledApps", "query", "app_debug"})
@javax.inject.Singleton()
public final class AppRepository {
    private final com.multiclone.app.core.virtualization.VirtualAppEngine virtualAppEngine = null;
    
    @javax.inject.Inject()
    public AppRepository(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.core.virtualization.VirtualAppEngine virtualAppEngine) {
        super();
    }
    
    /**
     * Get a list of all installed apps that can be cloned
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.multiclone.app.data.model.AppInfo> getInstalledApps() {
        return null;
    }
    
    /**
     * Search installed apps by name or package
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.multiclone.app.data.model.AppInfo> searchInstalledApps(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    /**
     * Get app info for a specific package
     */
    @org.jetbrains.annotations.Nullable()
    public final com.multiclone.app.data.model.AppInfo getAppInfo(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return null;
    }
}