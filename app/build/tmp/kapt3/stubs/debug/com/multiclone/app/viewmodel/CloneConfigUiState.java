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
 * UI state for the CloneConfigScreen
 */
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\tH\u00c6\u0003JK\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\tH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u00032\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\tH\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2 = {"Lcom/multiclone/app/viewmodel/CloneConfigUiState;", "", "isLoading", "", "selectedApp", "Lcom/multiclone/app/data/model/AppInfo;", "createShortcut", "launchAfterCreation", "cloneId", "", "error", "(ZLcom/multiclone/app/data/model/AppInfo;ZZLjava/lang/String;Ljava/lang/String;)V", "getCloneId", "()Ljava/lang/String;", "getCreateShortcut", "()Z", "getError", "getLaunchAfterCreation", "getSelectedApp", "()Lcom/multiclone/app/data/model/AppInfo;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class CloneConfigUiState {
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final com.multiclone.app.data.model.AppInfo selectedApp = null;
    private final boolean createShortcut = false;
    private final boolean launchAfterCreation = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String cloneId = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String error = null;
    
    /**
     * UI state for the CloneConfigScreen
     */
    @org.jetbrains.annotations.NotNull()
    public final com.multiclone.app.viewmodel.CloneConfigUiState copy(boolean isLoading, @org.jetbrains.annotations.Nullable()
    com.multiclone.app.data.model.AppInfo selectedApp, boolean createShortcut, boolean launchAfterCreation, @org.jetbrains.annotations.Nullable()
    java.lang.String cloneId, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        return null;
    }
    
    /**
     * UI state for the CloneConfigScreen
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * UI state for the CloneConfigScreen
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * UI state for the CloneConfigScreen
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public CloneConfigUiState() {
        super();
    }
    
    public CloneConfigUiState(boolean isLoading, @org.jetbrains.annotations.Nullable()
    com.multiclone.app.data.model.AppInfo selectedApp, boolean createShortcut, boolean launchAfterCreation, @org.jetbrains.annotations.Nullable()
    java.lang.String cloneId, @org.jetbrains.annotations.Nullable()
    java.lang.String error) {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.multiclone.app.data.model.AppInfo component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.multiclone.app.data.model.AppInfo getSelectedApp() {
        return null;
    }
    
    public final boolean component3() {
        return false;
    }
    
    public final boolean getCreateShortcut() {
        return false;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final boolean getLaunchAfterCreation() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCloneId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getError() {
        return null;
    }
}