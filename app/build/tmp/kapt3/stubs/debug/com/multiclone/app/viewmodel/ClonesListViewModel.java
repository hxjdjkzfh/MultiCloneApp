package com.multiclone.app.viewmodel;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.data.repository.CloneRepository;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the HomeScreen (ClonesListScreen)
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u000fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0015"}, d2 = {"Lcom/multiclone/app/viewmodel/ClonesListViewModel;", "Landroidx/lifecycle/ViewModel;", "cloneRepository", "Lcom/multiclone/app/data/repository/CloneRepository;", "launchCloneUseCase", "Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;", "(Lcom/multiclone/app/data/repository/CloneRepository;Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/multiclone/app/viewmodel/ClonesListUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "deleteClone", "", "cloneId", "", "launchClone", "loadClones", "Companion", "app_debug"})
public final class ClonesListViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.data.repository.CloneRepository cloneRepository = null;
    private final com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.multiclone.app.viewmodel.ClonesListUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.ClonesListUiState> uiState = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.multiclone.app.viewmodel.ClonesListViewModel.Companion Companion = null;
    private static final java.lang.String TAG = "ClonesListViewModel";
    
    @javax.inject.Inject()
    public ClonesListViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.CloneRepository cloneRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.viewmodel.ClonesListUiState> getUiState() {
        return null;
    }
    
    /**
     * Loads the list of clones
     */
    public final void loadClones() {
    }
    
    /**
     * Launches a cloned app
     */
    public final void launchClone(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
    }
    
    /**
     * Deletes a cloned app
     */
    public final void deleteClone(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/multiclone/app/viewmodel/ClonesListViewModel$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}