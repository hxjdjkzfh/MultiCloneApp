package com.multiclone.app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the app selection screen
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\fR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010\u00a8\u0006\u0018"}, d2 = {"Lcom/multiclone/app/viewmodel/AppSelectionViewModel;", "Landroidx/lifecycle/ViewModel;", "getInstalledAppsUseCase", "Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;", "(Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;)V", "_apps", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/multiclone/app/data/model/AppInfo;", "_isLoading", "", "_searchQuery", "", "apps", "Lkotlinx/coroutines/flow/StateFlow;", "getApps", "()Lkotlinx/coroutines/flow/StateFlow;", "isLoading", "searchQuery", "getSearchQuery", "loadApps", "", "searchApps", "query", "app_debug"})
public final class AppSelectionViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.domain.usecase.GetInstalledAppsUseCase getInstalledAppsUseCase = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.multiclone.app.data.model.AppInfo>> _apps = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.multiclone.app.data.model.AppInfo>> apps = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> searchQuery = null;
    
    @javax.inject.Inject()
    public AppSelectionViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.GetInstalledAppsUseCase getInstalledAppsUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.multiclone.app.data.model.AppInfo>> getApps() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSearchQuery() {
        return null;
    }
    
    public final void loadApps() {
    }
    
    public final void searchApps(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
}