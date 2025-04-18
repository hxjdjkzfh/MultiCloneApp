package com.multiclone.app.viewmodel;

import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.domain.usecase.DeleteCloneUseCase;
import com.multiclone.app.domain.usecase.GetClonesUseCase;
import com.multiclone.app.domain.usecase.LaunchCloneUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the clones list screen
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u000eJ\u000e\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u000eJ\u0006\u0010\u001d\u001a\u00020\u0019R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00100\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/multiclone/app/viewmodel/ClonesListViewModel;", "Landroidx/lifecycle/ViewModel;", "getClonesUseCase", "Lcom/multiclone/app/domain/usecase/GetClonesUseCase;", "launchCloneUseCase", "Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;", "deleteCloneUseCase", "Lcom/multiclone/app/domain/usecase/DeleteCloneUseCase;", "(Lcom/multiclone/app/domain/usecase/GetClonesUseCase;Lcom/multiclone/app/domain/usecase/LaunchCloneUseCase;Lcom/multiclone/app/domain/usecase/DeleteCloneUseCase;)V", "_clones", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lcom/multiclone/app/data/model/CloneInfo;", "_errorMessage", "", "_isLoading", "", "clones", "Lkotlinx/coroutines/flow/StateFlow;", "getClones", "()Lkotlinx/coroutines/flow/StateFlow;", "errorMessage", "getErrorMessage", "isLoading", "clearError", "", "deleteClone", "cloneId", "launchClone", "loadClones", "app_debug"})
public final class ClonesListViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.domain.usecase.GetClonesUseCase getClonesUseCase = null;
    private final com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase = null;
    private final com.multiclone.app.domain.usecase.DeleteCloneUseCase deleteCloneUseCase = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.multiclone.app.data.model.CloneInfo>> _clones = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.multiclone.app.data.model.CloneInfo>> clones = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _errorMessage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> errorMessage = null;
    
    @javax.inject.Inject()
    public ClonesListViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.GetClonesUseCase getClonesUseCase, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.LaunchCloneUseCase launchCloneUseCase, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.DeleteCloneUseCase deleteCloneUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.multiclone.app.data.model.CloneInfo>> getClones() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getErrorMessage() {
        return null;
    }
    
    public final void loadClones() {
    }
    
    public final void launchClone(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
    }
    
    public final void deleteClone(@org.jetbrains.annotations.NotNull()
    java.lang.String cloneId) {
    }
    
    public final void clearError() {
    }
}