package com.multiclone.app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.domain.usecase.GetInstalledAppsUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the AppSelectionScreen
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\rR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0011"}, d2 = {"Lcom/multiclone/app/viewmodel/AppSelectionViewModel;", "Landroidx/lifecycle/ViewModel;", "getInstalledAppsUseCase", "Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;", "(Lcom/multiclone/app/domain/usecase/GetInstalledAppsUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/multiclone/app/viewmodel/AppSelectionUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "filterApps", "", "query", "", "loadInstalledApps", "app_debug"})
public final class AppSelectionViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.domain.usecase.GetInstalledAppsUseCase getInstalledAppsUseCase = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.multiclone.app.viewmodel.AppSelectionUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.AppSelectionUiState> uiState = null;
    
    @javax.inject.Inject()
    public AppSelectionViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.GetInstalledAppsUseCase getInstalledAppsUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.AppSelectionUiState> getUiState() {
        return null;
    }
    
    /**
     * Loads the list of installed apps that can be cloned
     */
    public final void loadInstalledApps() {
    }
    
    /**
     * Filters the list of apps based on a search query
     */
    public final void filterApps(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
    }
}