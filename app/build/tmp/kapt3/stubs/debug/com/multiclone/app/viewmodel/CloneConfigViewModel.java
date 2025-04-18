package com.multiclone.app.viewmodel;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.multiclone.app.data.model.AppInfo;
import com.multiclone.app.data.model.CloneInfo;
import com.multiclone.app.data.repository.AppRepository;
import com.multiclone.app.domain.usecase.CreateCloneUseCase;
import com.multiclone.app.utils.IconUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel for the clone configuration screen
 */
@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010 \u001a\u00020!J\b\u0010\"\u001a\u00020!H\u0002J\u0006\u0010#\u001a\u00020!J\u0010\u0010$\u001a\u00020!2\b\u0010%\u001a\u0004\u0018\u00010\rJ\u000e\u0010&\u001a\u00020!2\u0006\u0010\'\u001a\u00020\u000fR\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0017R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00110\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0017R\u000e\u0010\u001d\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0017\u00a8\u0006("}, d2 = {"Lcom/multiclone/app/viewmodel/CloneConfigViewModel;", "Landroidx/lifecycle/ViewModel;", "appRepository", "Lcom/multiclone/app/data/repository/AppRepository;", "createCloneUseCase", "Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "(Lcom/multiclone/app/data/repository/AppRepository;Lcom/multiclone/app/domain/usecase/CreateCloneUseCase;Landroidx/lifecycle/SavedStateHandle;)V", "_cloneCreated", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/multiclone/app/data/model/CloneInfo;", "_customIcon", "Landroid/graphics/Bitmap;", "_displayName", "", "_isLoading", "", "_selectedApp", "Lcom/multiclone/app/data/model/AppInfo;", "cloneCreated", "Lkotlinx/coroutines/flow/StateFlow;", "getCloneCreated", "()Lkotlinx/coroutines/flow/StateFlow;", "customIcon", "getCustomIcon", "displayName", "getDisplayName", "isLoading", "packageName", "selectedApp", "getSelectedApp", "createClone", "", "loadAppInfo", "resetCloneCreated", "updateCustomIcon", "icon", "updateDisplayName", "name", "app_debug"})
public final class CloneConfigViewModel extends androidx.lifecycle.ViewModel {
    private final com.multiclone.app.data.repository.AppRepository appRepository = null;
    private final com.multiclone.app.domain.usecase.CreateCloneUseCase createCloneUseCase = null;
    private final java.lang.String packageName = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.multiclone.app.data.model.AppInfo> _selectedApp = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.data.model.AppInfo> selectedApp = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _displayName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> displayName = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<android.graphics.Bitmap> _customIcon = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<android.graphics.Bitmap> customIcon = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.multiclone.app.data.model.CloneInfo> _cloneCreated = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.data.model.CloneInfo> cloneCreated = null;
    
    @javax.inject.Inject()
    public CloneConfigViewModel(@org.jetbrains.annotations.NotNull()
    com.multiclone.app.data.repository.AppRepository appRepository, @org.jetbrains.annotations.NotNull()
    com.multiclone.app.domain.usecase.CreateCloneUseCase createCloneUseCase, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.data.model.AppInfo> getSelectedApp() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<android.graphics.Bitmap> getCustomIcon() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.multiclone.app.data.model.CloneInfo> getCloneCreated() {
        return null;
    }
    
    private final void loadAppInfo() {
    }
    
    public final void updateDisplayName(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void updateCustomIcon(@org.jetbrains.annotations.Nullable()
    android.graphics.Bitmap icon) {
    }
    
    public final void createClone() {
    }
    
    public final void resetCloneCreated() {
    }
}