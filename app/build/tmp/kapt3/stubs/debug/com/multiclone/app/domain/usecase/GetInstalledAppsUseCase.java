package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.domain.virtualization.VirtualAppEngine;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;

/**
 * Use case that retrieves all non-system apps installed on the device
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001f\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\b\b\u0002\u0010\t\u001a\u00020\nH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;", "", "virtualAppEngine", "Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;", "(Lcom/multiclone/app/domain/virtualization/VirtualAppEngine;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/multiclone/app/data/model/AppInfo;", "includeSystemApps", "", "app_debug"})
public final class GetInstalledAppsUseCase {
    private final com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine = null;
    
    @javax.inject.Inject()
    public GetInstalledAppsUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.virtualization.VirtualAppEngine virtualAppEngine) {
        super();
    }
    
    /**
     * Execute the use case to get all installed non-system apps
     *
     * @param includeSystemApps Whether to include system apps in the results (default: false)
     * @return A flow that emits a list of AppInfo objects
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.multiclone.app.data.model.AppInfo>> invoke(boolean includeSystemApps) {
        return null;
    }
}