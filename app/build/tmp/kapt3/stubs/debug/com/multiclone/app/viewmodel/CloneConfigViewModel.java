package com.multiclone.app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.domain.usecase.CreateCloneUseCase;
import com.multiclone.app.domain.usecase.CreateShortcutUseCase;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the CloneConfigScreen
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0011J\u000e\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u0017J\u000e\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u001cR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001d"}, d2 = {"Lcom/multiclone/app/viewmodel/CloneConfigViewModel;", "Landroidx/lifecycle/ViewModel;", "createCloneUseCase", "Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;", "createShortcutUseCase", "Lcom/multiclone/app/domain/usecase/CreateShortcutUseCase;", "launchCloneUseCase", "Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;", "(Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;Lcom/multiclone/app/domain/usecase/CreateShortcutUseCase;Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/multiclone/app/viewmodel/CloneConfigUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "createClone", "", "cloneName", "", "resetErrorState", "setCreateShortcut", "createShortcut", "", "setLaunchAfterCreation", "launchAfterCreation", "setSelectedApp", "appInfo", "Lcom/multiclone/app/data/model/AppInfo;", "app_debug"})
public final class CloneConfigViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.domain.usecase.CreateCloneUseCase createCloneUseCase = null;
    private final com.multiclone.app.domain.usecase.CreateShortcutUseCase createShortcutUseCase = null;
    private final com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.multiclone.app.viewmodel.CloneConfigUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.CloneConfigUiState> uiState = null;
    
    @javax.inject.Inject()
    public CloneConfigViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.CreateCloneUseCase createCloneUseCase, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.CreateShortcutUseCase createShortcutUseCase, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.CloneConfigUiState> getUiState() {
        return null;
    }
    
    /**
     * Sets the selected app to clone
     */
    public final void setSelectedApp(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.model.AppInfo appInfo) {
    }
    
    /**
     * Sets whether to create a shortcut for the clone
     */
    public final void setCreateShortcut(boolean createShortcut) {
    }
    
    /**
     * Sets whether to launch the clone after creation
     */
    public final void setLaunchAfterCreation(boolean launchAfterCreation) {
    }
    
    /**
     * Creates a clone of the selected app
     */
    public final void createClone(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneName) {
    }
    
    /**
     * Resets the error state
     */
    public final void resetErrorState() {
    }
}