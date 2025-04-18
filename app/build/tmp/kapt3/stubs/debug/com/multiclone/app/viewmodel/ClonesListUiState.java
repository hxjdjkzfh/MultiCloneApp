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
 * UI state for the ClonesListScreen
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B+\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\bH\u00c6\u0003J/\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00032\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\bH\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000e\u00a8\u0006\u0018"}, d2 = {"Lcom/multiclone/app/viewmodel/ClonesListUiState;", "", "isLoading", "", "clones", "", "Lcom/multiclone/app/data/model/CloneInfo;", "error", "", "(ZLjava/util/List;Ljava/lang/String;)V", "getClones", "()Ljava/util/List;", "getError", "()Ljava/lang/String;", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class ClonesListUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.multiclone.app.data.model.CloneInfo> clones = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    
    /**
     * UI state for the ClonesListScreen
     */
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.viewmodel.ClonesListUiState copy(boolean isLoading, @org.jetbrains.annotations.NotNull()
    java.util.List<com.multiclone.app.data.model.CloneInfo> clones, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        return null;
    }
    
    /**
     * UI state for the ClonesListScreen
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * UI state for the ClonesListScreen
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * UI state for the ClonesListScreen
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public ClonesListUiState() {
        super();
    }
    
    public ClonesListUiState(boolean isLoading, @org.jetbrains.annotations.NotNull()
    java.util.List<com.multiclone.app.data.model.CloneInfo> clones, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.multiclone.app.data.model.CloneInfo> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.multiclone.app.data.model.CloneInfo> getClones() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
}