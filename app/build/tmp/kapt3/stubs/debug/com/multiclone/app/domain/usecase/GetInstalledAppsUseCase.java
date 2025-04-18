package com.multiclone.app.domain.usecase;

import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.data.repository.AppRepository;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;

/**
 * Use case for retrieving installed applications
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0086B\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\bJ\u001f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\r"}, d2 = {"Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;", "", "appRepository", "Lcom/multiclone/app/data/repository/AppRepository;", "(Lcom/multiclone/app/data/repository/AppRepository;)V", "invoke", "", "Lcom/multiclone/app/data/model/AppInfo;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "search", "query", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class GetInstalledAppsUseCase {
    private final com.multiclone.app.data.repository.AppRepository appRepository = null;
    
    @javax.inject.Inject()
    public GetInstalledAppsUseCase(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.AppRepository appRepository) {
        super();
    }
    
    /**
     * Get all installed apps that can be cloned
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.multiclone.app.data.model.AppInfo>> continuation) {
        return null;
    }
    
    /**
     * Search for apps by name or package
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object search(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.multiclone.app.data.model.AppInfo>> continuation) {
        return null;
    }
}